from google.appengine.ext import db
import logging

#TODO: decide a method to detect remove discontinued conversations.
class Conversation(db.Model):
    """ For storing conversation sessions """
    user_1 = db.EmailProperty()
    user_2 = db.EmailProperty() 
    date_created = db.DateTimeProperty(auto_now_add = "True") # Creation date of conversation 
    date_joined = db.DateTimeProperty(auto_now = "True") # Join date of user_2

    #TODO: Clear this algorithm.
    def matchPeople(self, current_user):
        """ Function for matching current user with another user """ 
        dummy_email = "a@aa.aaa" # Will be set as partner if there does not exist any waiting user 
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 != :2 AND user_1 = :1  LIMIT 1", current_user, dummy_email) #If user_1 is current user
        query2 = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 = :1 LIMIT 1", current_user) #If user_2 is current user
        if query.count() == 1: #On a match for user_1
            self = query.get()
            return self.user_2
        elif query2.count() == 1: # On a match for user_2
            self = query2.get()
            return self.user_1
        
        else:
            
            query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 != :1 AND user_2 = :2 LIMIT 1",current_user, dummy_email) #If there is no current match
#TODO: Add an optimisation here by move the user_2 query above (query_2) to here. 
            if query.count() == 1: #Match this user with an existing pending user
                self = query.get()
                self.user_2 = current_user
                self.put()
                logging.debug("ikinci query geldi")
                return self.user_1 #R
            else: #Add this user to waiting list
                self.user_1 = current_user
                self.user_2 = "a@aa.aaa"
                self.put()
                logging.debug("ucuncu query geldi")
                return 0

    def remove(self, current_user, partner):
        query = db.GqlQuery("SELECT * FROM Conversation WHERE user_1 = :1 AND user_2 = :2  LIMIT 1", current_user, partner)
        query2 = db.GqlQuery("SELECT * FROM Conversation WHERE user_2 = :1 AND user_1 = :2  LIMIT 1", current_user, partner)
        if query.count() ==1:
            db.delete(query.get())
        else:
            db.delete(query2.get())
