import os
from consumer import RabbitMQConsumer
from dotenv import load_dotenv

load_dotenv()


#HOST = os.environ['RABBITMQ_HOST']
HOST = "rabbitmq1"
CAMEL_MQ_PROPERTIES = { "durable": "true", "auto_delete": "true" }
EXCHANGE_NAME = 'assess_health_data'
SERVICE_QUEUE_NAME = '7101'
REPLY_QUEUE_NAME = 'example_assess'


consumer_config = {
    "host": HOST,
    "port": 5672,
    "queue_name": SERVICE_QUEUE_NAME,
    "binding_key": SERVICE_QUEUE_NAME,
    "reply_queue_name": REPLY_QUEUE_NAME,
    "props": CAMEL_MQ_PROPERTIES,
    "retry_limit": 3
}

consumer = RabbitMQConsumer(consumer_config)

consumer.setup_queue(EXCHANGE_NAME, SERVICE_QUEUE_NAME)

try:
    consumer.channel.start_consuming()
except KeyboardInterrupt:
    consumer.channel.stop_consuming()
