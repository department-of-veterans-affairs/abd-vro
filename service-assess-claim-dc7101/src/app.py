import atexit
from consumer import RabbitMQConsumer
import os


HOST = 'rabbitmq1'
CAMEL_MQ_PROPERTIES = { "durable": "true", "auto_delete": "true" }
EXCHANGE_NAME = 'health-assess-exchange'
SERVICE_QUEUE_NAME = '7101'
REPLY_QUEUE_NAME = 'example_assess'


consumer_config = {
    "host": HOST,
    "port": 5672,
    "queue_name": SERVICE_QUEUE_NAME,
    "binding_key": SERVICE_QUEUE_NAME,
    "reply_queue_name": REPLY_QUEUE_NAME,
    "props": CAMEL_MQ_PROPERTIES,
    "retry_limit": 5
}

consumer = RabbitMQConsumer(consumer_config)

consumer.setup_queue(EXCHANGE_NAME, SERVICE_QUEUE_NAME)

def exit_handler():
  consumer.channel.stop_consuming()

atexit.register(exit_handler)

consumer.channel.start_consuming()