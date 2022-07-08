import atexit
from lib.consumer import RabbitMQConsumer
from config.settings import consumer_config


consumer = RabbitMQConsumer(consumer_config)
consumer.setup_queue("generate_pdf", "pdf_generator")

def exit_handler():
  consumer.channel.stop_consuming()

atexit.register(exit_handler)

consumer.channel.start_consuming()
