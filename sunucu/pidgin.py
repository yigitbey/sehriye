import dbus, dbus.glib, dbus.decorators, gobject , random

bus = dbus.SessionBus() 
obj = bus.get_object("im.pidgin.purple.PurpleService", "/im/pidgin/purple/PurpleObject") 
purple = dbus.Interface(obj, "im.pidgin.purple.PurpleInterface") 


protocols = {
    "jabber": "prpl-jabber",
} # List of protocols: http://developer.pidgin.im/wiki/prpl_id

### Config starts ###
account_name = "grotiiy@jabber.org" # Must already be logged on.
account_protocol = "jabber" # Change this with the platform
message_to_send = "test" # Change this with your spam
### Config ends ###

account = purple.PurpleAccountsFind(account_name, protocols[account_protocol]) # Get the account information
buddylist = purple.PurpleFindBuddies(account,'') # Get a list of all buddies
online_buddies = [] # Will contain online buddies
session = {}

# Search for online buddies, append them to online_buddies
for buddy in buddylist:
    if purple.PurpleBuddyIsOnline(buddy):
        online_buddies.append(buddy)

def test(conversation): 
    randP = random.choice(online_buddies) # Choose a buddy from online_buddies  
    message_to = purple.PurpleBuddyGetName(randP) # Get buddy's instance
    convers = purple.PurpleConversationNew(1, account, message_to) # Create a new conversation with the receiver
    print "ahmet"

# Our function to handle a received message
# TODO: Move to an abstract place.
def randomPerson(account, sender, message, conversation, flags):
    purple.PurpleConvImSend(purple.PurpleConvIm(session[conversation]), message) # Send the message to that conversation
#    purple.PurpleConvImSend(session[conversation], message) # Send the message to that conversation



# Signal handler for ReceivedImMsg
bus.add_signal_receiver(randomPerson,
                        dbus_interface="im.pidgin.purple.PurpleInterface",
                        signal_name="ReceivedImMsg") 

bus.add_signal_receiver(test,
                        dbus_interface="im.pidgin.purple.PurpleInterface",
                        signal_name="ConversationCreated") 


# Main loop
loop = gobject.MainLoop()
loop.run()    


