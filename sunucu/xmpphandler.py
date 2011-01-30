from google.appengine.api import xmpp
from google.appengine.ext import webapp

from conversation import Conversation
from custom_messages import *
import logging, time

class XMPPHandler(webapp.RequestHandler):
    """ Class for handling xmpp events """ 
    def customMessageHandler(self,conversation,message):
        """Handling custom messages imported from custom_messages.py"""
      
        #Disconnect
        if message == DELETE_CONVERSATION: #If a client wants to disconnect
            conversation.remove() # Remove the conversation session from database


    def post(self):
        """ Function to handle received messages """
        message = xmpp.Message(self.request.POST)
        sender = message.sender.split("/")[0] # Remove the identifier string from JID
        conversation = Conversation()
        partner = 0
        partner = conversation.matchPeople(sender) # Try to find a partner
        if partner != 0: #If we have partner
            
            #Initialize a conversation
            conversation.user_1 = sender
            conversation.user_2 = partner
   
            #Handling custom messages
            self.customMessageHandler(conversation,message.body) 
           
            # Send the message to partner even if the message is custom
            # This is to make sure that both clients realize actions
            xmpp.send_message(partner, message.body) 
                         
