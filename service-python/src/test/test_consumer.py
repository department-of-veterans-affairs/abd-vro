from lib.consumer import RabbitMQConsumer
import pika

consumer_settings = {
    "host": "127.0.0.1",
    "port": 5672,
    "exchange_name": "pdf_generator",
    "generate_queue_name": "generate_pdf",
    "fetch_queue_name": "fetch_pdf",
    "retry_limit": 3,
}

def test_valid_rabbitmq_connection():
    consumer = RabbitMQConsumer(consumer_settings)
    assert type(consumer.connection) == pika.BlockingConnection

def test_valid_queue_created():
    consumer = RabbitMQConsumer(consumer_settings)
    consumer.setup_queue(consumer_settings["exchange_name"], consumer_settings["queue_name"])
    queue_exists = consumer.channel.queue_declare(queue=consumer_settings["queue_name"], durable=True, passive=True)
    assert queue_exists