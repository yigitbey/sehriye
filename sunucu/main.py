from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from xmpphandler import XMPPHandler
from mainpage import MainPage
            

application = webapp.WSGIApplication([
        ('/', MainPage),
        ('/_ah/xmpp/message/chat/', XMPPHandler)],
                                     debug=True)

def main():
    run_wsgi_app(application)
    
if __name__ == "__main__":
    main()
