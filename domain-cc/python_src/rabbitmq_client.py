import pika

RABBIT_MQ_CONFIG = {
    'host': 'localhost',
    'port': 5672,
    'username': 'guest',
    'password': 'guest'
}

class RabbitMQClient:
    """ Class for consuming messages from RabbitMQ and forwarding them to the API endpoints. """
    def __init__(self):
        self.connection = pika.BlockingConnection()
        self.channel = self.connection.channel()
        self.channel.basic_consume(queue='hello', on_message_callback=self.on_message, auto_ack=True)
        self.channel.start_consuming()

    def on_message(self, channel, method, properties, body):
        print(f'received msg from queue: {body}')

    def call_endpoint(self, url, payload):
        pass