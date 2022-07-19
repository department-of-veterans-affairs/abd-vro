import atexit
from lib.consumer import RabbitMQConsumer
from config.settings import consumer_config


consumer = RabbitMQConsumer(consumer_config)
consumer.setup_queue(consumer_config["exchange_name"], consumer_config["queue_name"])

def exit_handler():
  consumer.channel.stop_consuming()

atexit.register(exit_handler)

consumer.channel.start_consuming()
