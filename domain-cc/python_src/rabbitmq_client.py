import pika
import httpx

RABBIT_MQ_CONFIG = {
    'host': 'localhost',
    'port': 5672,
    'username': 'guest',
    'password': 'guest',
    'queue_name': 'hello'
}

class RabbitMQClient:
    """ Class for consuming messages from RabbitMQ and forwarding them to the API endpoints. """
    def __init__(self):
        # self.connection = pika.adapters.select_connection.SelectConnection(
        #     pika.ConnectionParameters(host=RABBIT_MQ_CONFIG['host'], port=RABBIT_MQ_CONFIG['port'])
        # )
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(host=RABBIT_MQ_CONFIG['host'], port=RABBIT_MQ_CONFIG['port'])
        )
        self.channel = self.connection.channel()
        self.channel.queue_declare(queue=RABBIT_MQ_CONFIG["queue_name"])
        print('queue declared')
        self.channel.basic_consume(queue=RABBIT_MQ_CONFIG["queue_name"], on_message_callback=self.on_message, auto_ack=True)
        self.http_client = httpx.Client()
        print("rabbitmq client __init__()'d")

    def on_message(self, channel, method, properties, body):
        print(f'received msg from queue: {body}')

    def call_endpoint(self, url, payload):
        endpoint = url.split('domain-cc/')[-1]
        fastapi_url = f'http://localhost:8000/{endpoint}'
        return self.http_client.post(fastapi_url, data=payload)

    def run_continuously(self):
        try:
            self.channel.start_consuming()
        except KeyboardInterrupt:
            self.channel.stop_consuming()

def main():
    client = RabbitMQClient()  # url defined in RABBIT_MQ_CONFIG
    client.run_continuously()

if __name__ == '__main__':
    main()
