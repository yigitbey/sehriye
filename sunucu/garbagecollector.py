from conversation import Conversation
import datetime

class GarbageCollector():

    def detectDeadConversations(self):

        now = datetime.datetime.now()
        yesterday = now - datetime.timedelta(days=1)


        q = Conversation.all()
        q.filter(date_created < yesterday)


        return q



    def terminator(self):
        deadConversations = self.detectDeadConversations()
        for conversation in deadConversations:
            conversation.delete()
