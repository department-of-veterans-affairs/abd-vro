import pika
import json

import logging

from lib import main
from time import sleep


class RabbitMQConsumer:

    def __init__(self, config):
        self.config = config
        self.connection = self._create_connection()

    def __del__(self):
        self.connection.close()

    def _create_connection(self):
        for i in range(self.config["retry_limit"]):
                    try:
                        parameters = pika.ConnectionParameters(host=self.config["host"],
                        port = self.config["port"])
                        return pika.BlockingConnection(parameters)

                    except:
                        print(f"RabbitMQ Connection Failed. Retrying in 15s")
                        sleep(15)

    def rpc(self, body, route):

        logging.debug(f'CALL :{route}')

        decision_response = main.assess_hypertension(json.loads(body.decode('utf-8')))

        return decision_response

    def on_request(self, ch, method, props, body):

        route = method.routing_key
        response = self.rpc(body, route)
        print("sent")

        ch.basic_publish(exchange='',
                         routing_key=self.config["reply_queue_name"],
                         properties=pika.BasicProperties(correlation_id= \
                                                             props.correlation_id),
                         body=str(response))

        ch.basic_ack(delivery_tag=method.delivery_tag)

    def setup_queue(self, exchange_name, queue_name):
        channel = self.connection.channel()
        channel.exchange_declare(exchange=exchange_name, exchange_type="direct", durable=True, auto_delete=True)
        # This method creates or checks a queue
        channel.queue_declare(queue=queue_name)
        channel.queue_bind(queue=queue_name, exchange=exchange_name)
        channel.basic_qos(prefetch_count=1)

        channel.basic_consume(queue=queue_name, on_message_callback=self.on_request)
        self.channel = channel
        print(f" [*] Waiting for data for {queue_name}. To exit press CTRL+C")
