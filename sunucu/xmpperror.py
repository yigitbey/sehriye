from google.appengine.api import xmpp
from google.appengine.ext import webapp
from google.appengine.ext import db

from conversation import Conversation
from custom_messages import *
import logging, time

class XmppError(webapp.RequestHandler):
    def post(self):
        error_sender = self.request.get('from')
        error_stanza = self.request.get('stanza')
        logging.error('XMPP error received from %s (%s)', error_sender, error_stanza)

