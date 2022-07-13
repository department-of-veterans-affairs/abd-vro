from lib.consumer import RabbitMQConsumer
import pika

consumer_settings = {
    "host": "localhost",
    "port": 5672,
    "exchange": "generate_pdf",
    "queue_name": "pdf_generator",
    "retry_limit": 3,
}

def test_valid_rabbitmq_connection():
    consumer = RabbitMQConsumer(consumer_settings)
    assert type(consumer.connection) == pika.BlockingConnection

def test_valid_queue_created():
    consumer = RabbitMQConsumer(consumer_settings)
    consumer.setup_queue(consumer_settings["exchange"], consumer_settings["queue_name"])
    queue_exists = consumer.channel.queue_declare(queue=consumer_settings["queue_name"], durable=True, passive=True)
    assert queue_exists
    # # define your consumer
    # def on_message(channel, method_frame, header_frame, body):
    #     message = body.decode()
    #     # assert your message here
    #     # asset message == 'value'
    #     channel.basic_cancel('test-consumer')  # stops the consumer

    # # define your publisher
    # def publish_message(message):
    #     channel.basic_publish(exchange='', routing_key='', body=message')

    # publish('your message')
    # tag = channel.basic_consume(queue='queue', on_message_callback=on_message, consumer_tag='test-consumer')