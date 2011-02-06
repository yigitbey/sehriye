from google.appengine.ext import db
from google.appengine.api import xmpp

from geo.geomodel import GeoModel
from geo import geotypes
#from geoasync.geomodel import GeoModel
from custom_messages import *

import logging, random


#TODO: decide a method to detect remove discontinued conversations.
class Conversation(GeoModel):
    """ For storing conversation sessions """
    user_1 = db.EmailProperty() #JID of user_1
    user_2 = db.EmailProperty() #JID of user_2
    
    date_created = db.DateTimeProperty(auto_now_add = "True") # Creation date of conversation 
    date_joined = db.DateTimeProperty(auto_now = "True") # Join date of user_2
    is_started = db.BooleanProperty(default = False) # False if a conversation is on pending state
    
    # Each defaults to null from here. We can control if they are set by checking against it. 
    user_1_loc = db.GeoPtProperty() # Location of user_1. See http://code.google.com/appengine/docs/python/datastore/typesandpropertyclasses.html#GeoPtProperty
    user_2_loc = db.GeoPtProperty() # Location of user_2
    user_1_age = db.IntegerProperty() # Age of user_1
    user_2_age = db.IntegerProperty() # Age of user_2
    user_1_name = db.StringProperty() # Name of user_1    
    user_2_name = db.StringProperty() # Name of user_2    
    user_1_sex = db.IntegerProperty() # Sex of user_1. See http://en.wikipedia.org/wiki/ISO_5218
    user_2_sex = db.IntegerProperty() # Sex of user_2. See http://en.wikipedia.org/wiki/ISO_5218

    def getPartner(self,current_user):
        """
        Function to get the conversation

        If it finds an initiated conversation, it will return that conversation.
        Else it will return the dummy conversation created by calling function.
        """
        dummy_email = "a@aa.aaa" # Will be set as user_2 if there does not exist any waiting user 
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 != :2 AND user_1 = :1  LIMIT 1", current_user, dummy_email) #If user_1 is current user
        query2 = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 = :1 LIMIT 1", current_user) #If user_2 is current user
        if query.count() == 1: #On a match for user_1
            self = query.get()            
        elif query2.count() == 1:
            self = query2.get()
        return self
            

   #TODO: Clear this algorithm.
    def matchPeopleWithProximity(self, current_user, la,lo):
        """
        Function for matching current user with another user with their proximity

        It will populate self if it finds a match and will toggle is_started,
        otherwise it will create a new entry for this conversation (pending state)
        """ 
        #dummy_email = "a@aa.aaa" # Will be set as partner if there does not exist any waiting user 

        ### Get 10 nearest match
        #results = Conversation.proximity_fetch(
            #Conversation.all().filter('is_started', False), # Only conversations that have not started yet
            #db.GeoPt(la, lo), #With these coordinats
            #max_results=10, #Maximum number of results
            #max_distance=100000  # Within 100 km.
            #)
        ###

        ### Get 10 near match. See http://code.google.com/p/geomodel/wiki/Usage
        box_la = float(la) - 0.1
        box_lo = float(lo) - 0.1
        results = Conversation.bounding_box_fetch(
            Conversation.all().filter('is_started', False), # Only conversations that have not started yet
            geotypes.Box(float(la),float(lo),float(box_la),float(box_lo)),
            max_results=10, #Maximum number of results
            )
        ###


        ###If we have any potential match
        if len(results) > 0: 
            ### Start a conversation 
            self = random.choice(results)
            self.user_2 = current_user
            self.is_started = True
            self.put()
            ###

            logging.debug("ikinci query geldi")

            ### Send partner's JID to participants
            message_to_send = START_CONVERSATION + ":" + str(self.user_2) # Serialize START_CONVERSATION
            xmpp.send_message(self.user_1, message_to_send) # See custom_messages.py
            message_to_send = START_CONVERSATION + ":" + str(self.user_1) # Serialize START_CONVERSATION
            xmpp.send_message(self.user_2, message_to_send) # See custom_messages.py
            ###

        ###

        ### If nobody is waiting (:/) add this user to the waiting list
        else: #Add this user to waiting list

            ### Uncomment these two lines to ensure that nothing happens if a user sends a second PENDING_CONVERSATION
            #query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 = :1 AND user_2 = :2 LIMIT 1",current_user, dummy_email) #If they are not already on the waiting line
            #if query.count() == 0:
            ###

            self.location = db.GeoPt(la,lo) # See http://code.google.com/p/geomodel/wiki/Usage
            self.update_location()
            self.put()
            logging.debug("ucuncu query geldi")

        ###

    def remove(self):
        """Function to delete this conversation"""
        db.delete(self)
       
    def setTradeName(self, name):
        """
        Function to set name of a user

        If this is the first trade request on this conversation, it will just set the name for user_1
        Else it will send each participant's name to party. 
        (user_1 and user_2 are not sorted and same across each field. So clients will get both participant's data. 
        Clients need to parse and find the correct value from received data)
        """
        if self.user_1_name == None: # First trade request
            self.user_1_name = name
            self.put() # We don't need to save second user's name to database.     
        else:
            self.user_2_name = name # Second trade request
            message_to_send = TRADE_NAME + ":" + self.user_1_name + ":" + self.user_2_name # Serialize the custom message
            xmpp.send_message(self.user_1,message_to_send)
            xmpp.send_message(self.user_2,message_to_send)

    def setTradeAge(self, age):
        """
        Function to set age of a user

        If this is the first trade request on this conversation, it will just set the age for user_1
        Else it will send each participant's age to party.
        (user_1 and user_2 are not sorted and same across each field. So clients will get both participant's data. 
        Clients need to parse and find the correct value from received data)
        """
        if self.user_1_age == None: # First trade request
            self.user_1_age = int(age)
            self.put() # We don't need to save second user's name to database.          
        else:
            self.user_2_age = int(age) # Second trade request
            message_to_send = TRADE_AGE + ":" + str(self.user_1_age) + ":" + str(self.user_2_age) # Serialize the custom message
            xmpp.send_message(self.user_1,message_to_send)
            xmpp.send_message(self.user_2,message_to_send)

    def setTradeLocation(self, latitude, longtitude):
        """
        Function to set name of a user

        If this is the first trade request on this conversation, it will just set the location for user_1
        Else it will send each participant's location to party. 
        (user_1 and user_2 are not sorted and same across each field. So clients will get both participant's data. 
        Clients need to parse and find the correct value from received data)
        """
        if self.user_1_loc == None: # First trade request
            self.user_1_loc = db.GeoPt(latitude,longtitude)
            self.put() # We don't need to save second user's name to database.     
        else:
            self.user_2_loc = db.GeoPt(latitude,longtitude) # Second trade request
            message_to_send = TRADE_LOCATION + ":" + str(self.user_1_loc) + ":" + str(self.user_2_loc) # Serialize the custom message
            xmpp.send_message(self.user_1,message_to_send)
            xmpp.send_message(self.user_2,message_to_send)

    def setTradeSex(self, sex):
        """
        Function to set sex of a user

        If this is the first trade request on this conversation, it will just set the sex for user_1
        Else it will send each participant's sex to party. 
        (user_1 and user_2 are not sorted and same across each field. So clients will get both participant's data. 
        Clients need to parse and find the correct value from received data)
        """
        if self.user_1_sex == None: # First trade request
            self.user_1_sex = int(sex)
            self.put() # We don't need to save second user's name to database.     
        else:
            self.user_2_sex = int(sex) # Second trade request
            message_to_send = TRADE_SEX + ":" + str(self.user_1_sex) + ":" + str(self.user_2_sex) # Serialize the custom message
            xmpp.send_message(self.user_1,message_to_send)
            xmpp.send_message(self.user_2,message_to_send)

