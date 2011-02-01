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

        #Disconnect
        if message.body == DELETE_CONVERSATION: #If a client wants to disconnect
            conversation.remove() # Remove the conversation session from database
        
        #Pending Chat
        if message.body.split(":")[0] == PENDING_CHAT: # If a client wants to chat
            partner = conversation.matchPeople(message.sender) # Try to find a partner

            if partner == 0 : #If we don't have partner, set the location of the conversation
                conversation.setLocation(message.body.split(":")[1],message.body.split(":")[2])

            pass_message = 0 # Don't pass pending chat requests to clients
            
                             

        return pass_message
            
    def post(self):
        """ Function to handle received messages """
        message = xmpp.Message(self.request.POST)
        logging.info(message.to + message.sender + message.body)

        sender = message.sender.split("/")[0] # Remove the identifier string from JID
        conversation = Conversation(location = db.GeoPt(41,28))
        partner = 0
        conversation.user_2 = "a@aa.aaa"
        partner = conversation.matchPeople(sender) # Try to find a partner
        if partner != 0: #If we have partner
            
            # Initialize a conversation
            conversation.user_1 = sender
            conversation.user_2 = partner
   
            # Handling custom messages
            pass_message = self.customMessageHandler(conversation,message)
           
            if pass_message == 1: # If we need to pass this message to client
                xmpp.send_message(partner, message.body) # Send this message to partner
                         
