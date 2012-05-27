from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from xmpphandler import XMPPHandler
from xmpperror import XmppError            

from mainpage import MainPage

application = webapp.WSGIApplication([
        ('/_ah/xmpp/message/chat/', XMPPHandler), # Every XMPP message is handled by XMPPHandler class
        ('/_ah/xmpp/message/error/', XmppError), # For XMPP errors
        ],debug=True)

def main():
    """ Main function for GAE """
    run_wsgi_app(application)
    
if __name__ == "__main__":
    main()
