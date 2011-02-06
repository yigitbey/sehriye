from google.appengine.api import xmpp
from google.appengine.ext import webapp
from google.appengine.ext import db

from conversation import Conversation
from custom_messages import *
import logging, time

class XMPPHandler(webapp.RequestHandler):
    """ Class for handling xmpp events """ 
    def customMessageHandler(self,conversation,message):
        """Handling custom messages imported from custom_messages.py"""
      
        pass_message = 1 # Whether the message will be passed to partner
        command = message.body.split(":")[0] # Get the first part of the message

        ### Disconnect
        if command == DELETE_CONVERSATION: # If a client wants to disconnect
            conversation.remove() # Remove the conversation session from database
        ###

        ### Pending Chat
        elif command == PENDING_CONVERSATION: # If a client wants a conversation
            sender = message.sender.split("/")[0] # Remove the identifier string from JID
            latitude = message.body.split(":")[1] # Latitude of user
            longtitude = message.body.split(":")[2] # Longtitude of user

            partner = conversation.matchPeopleWithProximity(sender, latitude, longtitude) # Try to find a partner

            pass_message = 0 # Don't pass pending chat requests to clients
        ###

        ### Trade Name
        elif command == TRADE_NAME: # If a clients wants to send his name
            sender = message.sender.split("/")[0] # Remove the identifer string from JID
            name = message.body.split(":")[1] # Name of the user
            conversation.setTradeName(name)
            pass_message = 0
        ###

        ### Trade Age
        elif command == TRADE_AGE: # If a clients wants to send his age
            sender = message.sender.split("/")[0] # Remove the identifer string from JID
            age = message.body.split(":")[1] # Age of the user
            conversation.setTradeAge(age)
            pass_message = 0
        ###

        ### Trade Location
        elif command == TRADE_LOCATION: # If a clients wants to send his location
            sender = message.sender.split("/")[0] # Remove the identifer string from JID
            latitude = message.body.split(":")[1] # Latitude of the user
            longtitude = message.body.split(":")[2] # Longtitude of the user
            conversation.setTradeLocation(latitude,longtitude)
            pass_message = 0
        ###

        ### Trade Sex
        elif command == TRADE_SEX: # If a clients wants to send his sex (#!?^ FUCK gender specific pronouns #!?^)
            sender = message.sender.split("/")[0] # Remove the identifer string from JID
            sex = message.body.split(":")[1] # Sex of the user
            conversation.setTradeSex(sex) # See http://en.wikipedia.org/wiki/ISO_5218
            pass_message = 0
        ###

            


        return pass_message
            
    def post(self):
        """ Function to handle received messages """
        dummy_email = "a@aa.aaa"
        message = xmpp.Message(self.request.POST)
        pass_message = 1 

        sender = message.sender.split("/")[0] # Remove the identifier string from JID
        conversation = Conversation(user_1 = sender, user_2 = dummy_email ,location = db.GeoPt(41,28))
        
        partner = conversation.getPartner(sender) # Try to get a partner
        if partner != 0:
            conversation.user_2 = partner
 
        if message.body[0] == "|": # If this is a custom message
            pass_message = self.customMessageHandler(conversation,message)

        if partner != 0: #If we have partner
            
            # Initialize a conversation
   
            # Handling custom messages
           
            if pass_message == 1: # If we need to pass this message to client
                xmpp.send_message(partner, message.body) # Send this message to partner
                         
