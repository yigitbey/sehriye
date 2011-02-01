from google.appengine.ext import db
from google.appengine.api import xmpp

from geo.geomodel import GeoModel
from custom_messages import *

import logging, random


#TODO: decide a method to detect remove discontinued conversations.
class Conversation(GeoModel):
    """ For storing conversation sessions """
    user_1 = db.EmailProperty()
    user_2 = db.EmailProperty() 
    
    date_created = db.DateTimeProperty(auto_now_add = "True") # Creation date of conversation 
    date_joined = db.DateTimeProperty(auto_now = "True") # Join date of user_2
    is_started = db.BooleanProperty(default = False)
    user_1_loc = db.GeoPtProperty() # Location of user_1. See http://code.google.com/appengine/docs/python/datastore/typesandpropertyclasses.html#GeoPtProperty
    user_2_loc = db.GeoPtProperty() # Location of user_2
    user_1_age = db.IntegerProperty() # Age of user_1
    user_2_age = db.IntegerProperty() # Age of user_2
    user_1_name = db.StringProperty() # Name of user_1    
    user_2_name = db.StringProperty() # Name of user_2    
    user_1_sex = db.IntegerProperty() # Sex of user_1. See http://en.wikipedia.org/wiki/ISO_5218
    user_2_sex = db.IntegerProperty() # Sex of user_2

    def getPartner(self,current_user):
        """Function to get partner"""
        dummy_email = "a@aa.aaa" # Will be set as user_2 if there does not exist any waiting user 
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 != :2 AND user_1 = :1  LIMIT 1", current_user, dummy_email) #If user_1 is current user
        query2 = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 = :1 LIMIT 1", current_user) #If user_2 is current user
        if query.count() == 1: #On a match for user_1
            self = query.get()
            return self.user_2
        elif query2.count() == 1: # On a match for user_2
            self = query2.get()
            return self.user_1
        else:
            return 0

   #TODO: Clear this algorithm.
    def matchPeople(self, current_user):
        """ Function for matching current user with another user """ 
        dummy_email = "a@aa.aaa" # Will be set as partner if there does not exist any waiting user 
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 != :1 AND user_2 = :2 LIMIT 1",current_user, dummy_email) #If there is no current match
#TODO: Add an optimisation here by move the user_2 query above (query_2) to here. 
        if query.count() == 1: #Match this user with an existing pending user
            self = query.get()
            self.user_2 = current_user
            self.is_started = True
            self.put()
            logging.debug("ikinci query geldi")
            xmpp.send_message(self.user_1,START_CONVERSATION) # See custom_messages.py
            xmpp.send_message(self.user_2,START_CONVERSATION) # See custom_messages.py
            return self.user_1 #Return partner
        else: #Add this user to waiting list
            query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 = :1 AND user_2 = :2 LIMIT 1",current_user, dummy_email) #If they are not already on the waiting line
            if query.count() == 0:
                self.user_1 = current_user
                self.user_2 = "a@aa.aaa"
                #self.location = db.GeoPt(41,28) # See http://code.google.com/p/geomodel/wiki/Usage
                #self.update_location()
                self.put()
            logging.debug("ucuncu query geldi")
            return 0

   #TODO: Clear this algorithm.
    def matchPeopleWithProximity(self, current_user, la,lo):
        """ Function for matching current user with another user with their proximity""" 
        dummy_email = "a@aa.aaa" # Will be set as partner if there does not exist any waiting user 

        results = Conversation.proximity_fetch(
            Conversation.all().filter('is_started', False), # Only conversations that have not started yet
            db.GeoPt(la, lo), #With these coordinats
            max_results=10, #Maximum number of results
            max_distance=100000  # Within 100 km.
            ) 
        if len(results) > 0: #Match this user with an existing pending user
            self = random.choice(results)
            self.user_2 = current_user
            self.is_started = True
            self.put()
            logging.debug("ikinci query geldi")
            xmpp.send_message(self.user_1,START_CONVERSATION) # See custom_messages.py
            xmpp.send_message(self.user_2,START_CONVERSATION) # See custom_messages.py
            return self.user_1 #Return partner

        else: #Add this user to waiting list
            query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 = :1 AND user_2 = :2 LIMIT 1",current_user, dummy_email) #If they are not already on the waiting line
            if query.count() == 0:
                self.user_1 = current_user
                self.user_2 = "a@aa.aaa"
                self.location = db.GeoPt(la,lo) # See http://code.google.com/p/geomodel/wiki/Usage
                self.update_location()
                self.put()
            logging.debug("ucuncu query geldi")
            return 0


    def remove(self):
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 = :1 AND user_2 = :2  LIMIT 1", self.user_1, self.user_2)
        query2 = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 = :1 AND user_1 = :2  LIMIT 1", self.user_1, self.user_2)
        if query.count() ==1:
            db.delete(query.get())
        else:
            db.delete(query2.get())

    def setLocation(self,la,lo):
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 = :1 AND user_2 = :2  LIMIT 1", self.user_1, "a@aa.aaa")
        conversation = query.get()
        conversation.location = db.GeoPt(la,lo)
        logging.debug(str(conversation.location))
        conversation.update_location()
        conversation.put()
        
