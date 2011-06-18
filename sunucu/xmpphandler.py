from google.appengine.api import xmpp
from google.appengine.ext import webapp
from google.appengine.ext import db

from conversation import Conversation
from custom_messages import *
import logging, time

class XMPPHandler(webapp.RequestHandler):
    """ Class for handling xmpp events """ 
    def customMessageHandler(self,conversation,message):
        """
        Handling custom messages imported from custom_messages.py
        
        @param conversation: Current Conversation
        @type conversation: Conversation from conversation.py
        @param message: Message to be handled
        @type message: Class consisting of sender,body,to

        @rtype: bool
        @return: True if the message will be passed to partner, False otherwise.
        """
      
        command = message.body.split(":")[0] #: Get the first part of the message

        ### Disconnect
        if command == DELETE_CONVERSATION: # If a client wants to disconnect
            conversation.remove() # Remove the conversation session from database
            pass_message = True
        ###

        ### Pending Chat
        elif command == PENDING_CONVERSATION: # If a client wants a conversation
            sender = message.sender.split("/")[0] #: Remove the identifier string from JID
            latitude = message.body.split(":")[1] #: Latitude of user
            longtitude = message.body.split(":")[2] #: Longtitude of user

            conversation.matchPeopleWithProximity(sender, latitude, longtitude) # Try to find a partner

            pass_message = False # Don't pass pending chat requests to clients
        ###

        ### Trade Name
        elif command == TRADE_NAME: # If a clients wants to send his name
            sender = message.sender.split("/")[0] #: Remove the identifer string from JID
            name = message.body.split(":")[1] #: Name of the user
            conversation.setTradeName(name)
            pass_message = False
        ###

        ### Trade Age
        elif command == TRADE_AGE: # If a clients wants to send his age
            sender = message.sender.split("/")[0] #: Remove the identifer string from JID
            age = message.body.split(":")[1] #: Age of the user
            conversation.setTradeAge(age)
            pass_message = False
        ###

        ### Trade Location
        elif command == TRADE_LOCATION: # If a clients wants to send his location
            sender = message.sender.split("/")[0] #: Remove the identifer string from JID
            latitude = message.body.split(":")[1] #: Latitude of the user
            longtitude = message.body.split(":")[2] #: Longtitude of the user
            conversation.setTradeLocation(latitude,longtitude)
            pass_message = False
        ###

        ### Trade Sex
        elif command == TRADE_SEX: # If a clients wants to send his sex (#!?^ FUCK gender specific pronouns #!?^)
            sender = message.sender.split("/")[0] #: Remove the identifer string from JID
            sex = message.body.split(":")[1] #: Sex of the user. See U{http://en.wikipedia.org/wiki/ISO_5218}
            conversation.setTradeSex(sex) 
            pass_message = False
        ###

        elif command == TRADE_PHONE: # If a clients want to send his phone number
            sender = message.sender.split("/")[0] #: Remove the identifier string from JID
            phone = message.body.split(":")[1] #: Phone number of the user
            conversation.setTradePhone(phone)
            pass_message = False #: Sets if the message will be passed to partner

        return pass_message
            
    def post(self):
        """ 
        Function to handle received messages

        @rtype: None
        """
        #TODO: Delete this variable.
        dummy_email = "a@aa.aaa" #: Dummy Email. Will be used for user_2 if there is no match.
        message = xmpp.Message(self.request.POST) 
        pass_message = True #: Whether the message will be passed to partner

        sender = message.sender.split("/")[0] # Remove the identifier string from JID
        conversation = Conversation(user_1 = sender, user_2 = dummy_email ,location = db.GeoPt(41,28)) # Create a new conversation
        conversation = conversation.getPartner(sender) # Try to get a partner

        if message.body[0] == "|": # If this is a custom message
            pass_message = self.customMessageHandler(conversation,message)

        if conversation.is_started == True: # If this conversation has started
            if conversation.user_1 == sender: 
                partner = conversation.user_2
            else:
                partner = conversation.user_1
            
            if pass_message == True: # If we need to pass this message to client (See custom messages)
                xmpp.send_message(partner, message.body) # Send this message to partner
                         
