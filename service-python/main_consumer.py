from time import sleep, time
from lib.queues import queue_setup

import pika
import logging
import os
import atexit

logging.basicConfig(level=logging.INFO)

CONSUMER_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
		"username": os.environ.get("RABBITMQ_PLACEHOLDERS_USERNAME", "guest"),
		"password": os.environ.get("RABBITMQ_PLACEHOLDERS_USERPASSWORD", "guest"),
    "port": 5672,
    "retry_limit": 3,
		"timeout": 60 * 60 * 3 # 3 hours
}


class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()
		self.setup_queues()


	def __del__(self):
		self.connection.close()


	def _create_connection(self):
		credentials = pika.PlainCredentials(self.config["username"], self.config["password"])
		for i in range(self.config["retry_limit"]):
			try:
				parameters = pika.ConnectionParameters(host=self.config["host"], port=self.config["port"], credentials=credentials)
				return pika.BlockingConnection(parameters)
			except:
				logging.warning(f"RabbitMQ Connection Failed. Retrying in 30s")
				sleep(30)


	def setup_queues(self):
		channel = self.connection.channel()
		queue_setup(channel)
		self.channel = channel


if __name__ == "__main__":

	start_timer = None
	current_timer = None

	while(True):
		try:
				consumer = RabbitMQConsumer(CONSUMER_CONFIG)


				consumer.channel.start_consuming()
		except:
			if start_timer is None:
				start_timer = time()
				current_timer = 0
			else:
				current_timer = time() - start_timer
			if current_timer < CONSUMER_CONFIG["timeout"]:
				logging.warning("Connection was closed. Retrying...")
				continue
			else:
				break