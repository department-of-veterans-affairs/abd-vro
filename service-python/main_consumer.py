from time import sleep
from lib.queues import queue_setup

import pika
import logging
import os
import atexit

logging.basicConfig(level=logging.INFO)

CONSUMER_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "port": 5672,
    "retry_limit": 3,
}


class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()
		self.setup_queues()


	def __del__(self):
		self.connection.close()


	def _create_connection(self):
		for i in range(self.config["retry_limit"]):
			try:
				parameters = pika.ConnectionParameters(host=self.config["host"], port = self.config["port"])
				return pika.BlockingConnection(parameters)
			except:
				logging.warn(f"RabbitMQ Connection Failed. Retrying in 15s")
				sleep(15)


	def setup_queues(self):
		channel = self.connection.channel()
		queue_setup(channel)
		self.channel = channel


if __name__ == "__main__":
	consumer = RabbitMQConsumer(CONSUMER_CONFIG)

	atexit.register(consumer.channel.stop_consuming)

	consumer.channel.start_consuming()