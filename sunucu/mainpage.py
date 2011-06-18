from google.appengine.ext import webapp

class MainPage(webapp.RequestHandler):
    """ Class for root url """
    def get(self):
        """ Placeholder function for mainpage """
        self.response.headers['Content-Type'] = 'text/plain'


### Comment test
