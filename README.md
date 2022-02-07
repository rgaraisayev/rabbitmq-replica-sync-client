# rabbitmq-replica-sync-client


Used: rabbitmq, spring boot

# How it works

Client publishes syncable(insert,update,delete) data to rabbitmq queue and asyncroniously listens to reply-to queue to update state of synced data(synced or failed)
Server receives from queue and sends response back (https://github.com/rgaraisayev/rabbitmq-replica-sync-server)


Rabbit mq convertSendAndReceive  
