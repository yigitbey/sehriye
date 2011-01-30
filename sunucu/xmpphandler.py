from google.appengine.api import xmpp
from google.appengine.ext import webapp

from conversation import Conversation
from custom_messages import *
import logging, time

class XMPPHandler(webapp.RequestHandler):
    """ Class for handling xmpp events """ 
    def post(self):
        """ Function to handle received messages """
        message = xmpp.Message(self.request.POST)
        sender = message.sender.split("/")[0] # Remove the identifier string from JID
        conversation = Conversation()
        partner = 0
        retry = 0
        while (partner == 0 and retry < 10 ): # Until a partner is found, Google kills the loop if it takes too long
            retry += 1
            logging.debug(retry)
            partner = conversation.matchPeople(sender) # Try to find a partner
            if partner != 0: #If we have partner
                if message.body == DELETE_CONVERSATION: #If a client wants to disconnect
                    conversation.remove(sender,partner) # Remove the conversation session from database

                # Send the message to partner even if the message is "/DELETETHISCONVERSATION"
                # This is to make sure that both clients realize the disconnection
                xmpp.send_message(partner, message.body) 

                break # Break the partner searching loop, if any found
            time.sleep(1)
                    



        

    

