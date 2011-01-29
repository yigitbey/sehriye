import logging, random
from google.appengine.api import xmpp
from google.appengine.ext import webapp
from google.appengine.ext import db
from google.appengine.ext.webapp.util import run_wsgi_app


class User(db.Model):
    """ For storing current online users. """
    address = db.EmailProperty() # To store xmpp address of user
    is_online = db.BooleanProperty() # To store the status of user. 
    rand = db.FloatProperty() # To workaround GQL's limitation of a random entry returning quer
    # TODO: Decide a way to store a list of online users  
    # TODO: Implement a mechanism to look for online users every x minute,
    #       remove others from this list.

    def getRandom(self):
        """ 
        This function returns a random user 
        Since GQL objects does not have len(), it founds its way by doing a dirty hack:
        Storing a random floating number for every user. And querying another random number
        This can be a problem if number of active users becomes greater then the precision 
        of stored random value. 
        And also our random number can be bigger then any entry on the database.
        """

        self.is_online = False
        
        while (self.is_online == False):
            random.seed()
            rand = random.random() # To select a random entry from User table
            logging.debug(rand)
            query = db.GqlQuery("SELECT * FROM User WHERE rand > :1 AND is_online = True ORDER BY rand LIMIT 1 ", rand)
            self = query.get()
            
        return self
        
    
    def register(self,sender, is_persistent = True): 
        """ Registering a user to our datastore """
        
        ### Registering our user to current users
        # TODO: avoid duplicate entries.
        self.address = sender
        self.is_online = True
        self.rand = random.random()
        self.put()
        ###



# TODO: decide if this is necessary.
class PersistentUser(db.Model):
    """ For storing every user ever tried this app """
    address = db.EmailProperty()

    def register(self,sender):
        """ Registering a user to our datastore """
        # Registering our user as a persistent user
        # TODO: avoid duplicate entries.
        self.address = sender
        self.put()
        ###


class Conversation(db.Model):
    """ For storing conversation sessions """
    user_1 = User()
    user_2 = User()


class XMPPHandler(webapp.RequestHandler):
    def post(self):
        """ Function to handle received messages """
        message = xmpp.Message(self.request.POST)
        
        #        registerUser(message.sender)
        user = User()
        user.register(message.sender)
        puser = PersistentUser()
        puser.register(message.sender)

        message_to = user.getRandom()
        logging.debug("kisi:" + message_to.address)


        logging.info("gelen mesaj: " + message.body) # TODO: remove this logging
        if message.body == 'osman abi evde mi?':
            message.reply("evde.")
            logging.info("verilen cevap: " + "evde") # TODO: remove this logging
            

class MainPage(webapp.RequestHandler):
    def get(self):
        """ Placeholder function for mainpage """
        self.response.headers['Content-Type'] = 'text/plain'

        user = User()
        user.register("mainpage")
        puser = PersistentUser()
        puser.register("mainpage")

        user = user.getRandom()
        
        
        self.response.out.write(user.address)


application = webapp.WSGIApplication([
        ('/', MainPage),
        ('/_ah/xmpp/message/chat/', XMPPHandler)],
                                     debug=True)

def main():
    run_wsgi_app(application)
    
if __name__ == "__main__":
    main()
