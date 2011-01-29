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


# TODO: decide if this is necessary.
class PersistentUser(db.Model):
    """ For storing every user ever tried this app"""
    address = db.EmailProperty()


def getRandomUser():
    """ 
    This function returns a random user 
    Since GQL objects does not have len(), it founds its way by doing a dirty hack:
    Storing a random floating number for every user. And querying another random number
    This can be a problem if number of active users becomes greater then the precision 
    of stored random value. 
    """
    user = User()
    user.is_online = False

    rand = random.random() # To select a random entry from User table
    while (user.is_online == False):
        query = db.GqlQuery("SELECT * FROM User WHERE rand > :1 AND is_online = True ORDER BY rand LIMIT 1 ", rand)
        user = query.get()

    return user

    
def registerUser(sender, is_persistent = True): 
    """ Registering a user to our datastore """

    ### Registering our user to current users
    user = User()
    user.address = sender
    user.is_online = True
    user.rand = random.random()
    user.put()
    ###

    ### Registering our user as a persistent user
    # TODO: avoid duplicate entries.
    if is_persistent == True:
        puser = PersistentUser()
        puser.address = sender
        puser.put()
    ###


class XMPPHandler(webapp.RequestHandler):
    def post(self):
        """ Function to handle received messages """
        message = xmpp.Message(self.request.POST)
        
        registerUser(message.sender)


        logging.info("gelen mesaj: " + message.body) # TODO: remove this logging
        if message.body == 'osman abi evde mi?':
            message_to = getRandomUser()
            logging.debug("kisi:" , message_to.address)
            message.reply("evde.")
            logging.info("verilen cevap: " + "evde") # TODO: remove this logging
            

class MainPage(webapp.RequestHandler):
    def get(self):
        """ Placeholder function for mainpage """
        self.response.headers['Content-Type'] = 'text/plain'

        user = getRandomUser()
        self.response.out.write(user.address)


application = webapp.WSGIApplication([
        ('/', MainPage),
        ('/_ah/xmpp/message/chat/', XMPPHandler)],
                                     debug=True)

def main():
    run_wsgi_app(application)
    
if __name__ == "__main__":
    main()
