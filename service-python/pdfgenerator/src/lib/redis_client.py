from time import sleep

import logging
import redis

logging.basicConfig(level=logging.INFO)

class RedisClient:

	def __init__(self, config):
		self.config = config
		self.client = self._create_client()


	def _create_client(self):
		for i in range(self.config["retry_limit"]):
			try:
				client = redis.Redis(host=self.config["host"], port = self.config["port"], password = self.config["password"])
				logging.warning(f"Redis Connected: {client}")
				return client
			except:
				logging.warning(f"Redis Connection Failed. Retrying in 15s")
				sleep(15)

	
	def exists(self, key):
		return self.client.exists(key)


	def save_data(self, key, value):
		self.client.set(key, value)
		self.client.expire(key, self.config["expiration"])


	def get_data(self, key):
		return self.client.get(key)