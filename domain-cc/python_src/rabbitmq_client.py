import pika
import httpx
from hoppy import Service as HoppyService

RABBIT_MQ_CONFIG = {  # define this as a custom type
    "host": 'localhost',
    "port": 5672,
    "username": 'guest',
    "password": 'guest',
    "queue_name": 'hello',
    "exchange_name": "contention-classification-exchange",
    "service_queue_name": "domain-cc-classify",
    "retry_limit": 3,
    "timeout": 60 * 2,  # rename this to "timeout_seconds"
}
http_client = httpx.Client()

# class RabbitMQClient:
#     """ Class for consuming messages from RabbitMQ and forwarding them to the API endpoints. """
#     def __init__(self):
#         # self.connection = pika.adapters.select_connection.SelectConnection(
#         #     pika.ConnectionParameters(host=RABBIT_MQ_CONFIG['host'], port=RABBIT_MQ_CONFIG['port'])
#         # )
#         self.connection = pika.BlockingConnection(
#             pika.ConnectionParameters(host=RABBIT_MQ_CONFIG['host'], port=RABBIT_MQ_CONFIG['port'])
#         )
#         self.channel = self.connection.channel()
#         self.channel.queue_declare(queue=RABBIT_MQ_CONFIG["queue_name"])
#         print('queue declared')
#         self.channel.basic_consume(queue=RABBIT_MQ_CONFIG["queue_name"], on_message_callback=self.on_message, auto_ack=True)
#         self.http_client = httpx.Client()
#         print("rabbitmq client __init__()'d")
#
#     def on_message(self, channel, method, properties, body):
#         print(f'received msg from queue: {body}')
#
#     def call_endpoint(self, url, payload):
#         endpoint = url.split('domain-cc/')[-1]
#         fastapi_url = f'http://localhost:8000/{endpoint}'
#         return self.http_client.post(fastapi_url, data=payload)
#
#     def run_continuously(self):
#         try:
#             self.channel.start_consuming()
#         except KeyboardInterrupt:
#             self.channel.stop_consuming()

# ambiguous types, see https://github.com/department-of-veterans-affairs/abd-vro/commit/260071d0a1f59ad0c44c78cb96dc2e511e59e3ee#diff-03e72a5bda60963cfbc4f88ab305c195a4f57d88ead1a0dafacdf2061c1cca41R9
def call_endpoint(message, routing_key):
    print(f'message: {message}')
    print(f'routing_key: {routing_key}')
    fastapi_url = f'http://localhost:8000/{message["endpoint"]}'
    payload = message["payload"]
    return http_client.post(fastapi_url, data=payload).json()

def main():
    # client = RabbitMQClient()  # url defined in RABBIT_MQ_CONFIG
    # client.run_continuously()
    print('initializing Hoppy thing')
    rabbitmq_client = HoppyService(
        config=RABBIT_MQ_CONFIG,
        exchange=RABBIT_MQ_CONFIG["exchange_name"],
        consumers={
            RABBIT_MQ_CONFIG["service_queue_name"]: call_endpoint
        }
    )
    print('run()ing it')
    rabbitmq_client.run()

if __name__ == '__main__':
    main()
