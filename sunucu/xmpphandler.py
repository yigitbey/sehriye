from google.appengine.api import xmpp
from google.appengine.ext import webapp


from conversation import Conversation
import logging, time

class XMPPHandler(webapp.RequestHandler):
    """ Class for handling xmpp events """ 
    def post(self):
        """ Function to handle received messages """
        message = xmpp.Message(self.request.POST)
        
        #        registerUser(message.sender)

        sender = message.sender.split("/")[0]
        conversation = Conversation()
        partner = 0
        while (partner == 0):
            partner = conversation.matchPeople(sender)
            time.sleep(1)
            

        logging.debug("kisi:" + partner)
        
        message.reply(partner)
        
        logging.info("gelen mesaj: " + message.body) # TODO: remove this logging
        if message.body == 'osman abi evde mi?':
            message.reply("evde.")
            logging.info("verilen cevap: " + "evde") # TODO: remove this logging
