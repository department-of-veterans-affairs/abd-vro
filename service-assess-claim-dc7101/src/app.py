import atexit
import os
from consumer import RabbitMQConsumer

EXCHANGE_NAME = 'health-assess-exchange'
SERVICE_QUEUE_NAME = '7101'

consumer_config = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "port": 5672,
    "exchange":EXCHANGE_NAME,
    "queue_name": SERVICE_QUEUE_NAME,
    "binding_key": SERVICE_QUEUE_NAME,
    "reply_queue_name": 'example_assess',
    "props": { "durable": "true", "auto_delete": "true" },
    "retry_limit": 5
}

consumer = RabbitMQConsumer(consumer_config)


def exit_handler():
  consumer.channel.stop_consuming()

atexit.register(exit_handler)

consumer.channel.start_consuming()