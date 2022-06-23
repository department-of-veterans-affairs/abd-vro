from lib.consumer import RabbitMQConsumer
from config.settings import consumer_config

consumer = RabbitMQConsumer(consumer_config)

consumer.setup_queue("generate_pdf", "pdf_generator")

try:
  consumer.channel.start_consuming()
except KeyboardInterrupt:
  consumer.channel.stop_consuming()
