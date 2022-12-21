import logging
import os
from time import sleep, time

import logging_setup
import pika
from lib.queues import queue_setup

logger = logging_setup.set_format()

CONSUMER_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "username": os.environ.get("RABBITMQ_PLACEHOLDERS_USERNAME", "guest"),
    "password": os.environ.get("RABBITMQ_PLACEHOLDERS_USERPASSWORD", "guest"),
    "port": int(os.environ.get("RABBITMQ_PORT", 5672)),
    "retry_limit": int(os.environ.get("RABBITMQ_RETRY_LIMIT", 3)),
    # 3 hours
    "timeout": int(os.environ.get("RABBITMQ_TIMEOUT", 60 * 60 * 3))
}


class RabbitMQConsumer:

    def __init__(self, config):
        self.config = config
        self.connection = self._create_connection()
        if self.connection:
            self.setup_queues()

    def __del__(self):
        if self.connection:
            self.connection.close()

    def _create_connection(self):
        credentials = pika.PlainCredentials(self.config["username"], self.config["password"])
        for i in range(self.config["retry_limit"]):
            try:
                parameters = pika.ConnectionParameters(host=self.config["host"], port=self.config["port"], credentials=credentials)
                return pika.BlockingConnection(parameters)
            except Exception as e:
                logging.warning(e, exc_info=True)
                logging.warning(f"RabbitMQ Connection Failed. Retrying in 30s ({i + 1}/{self.config['retry_limit']})")
                sleep(30)
        return None

    def setup_queues(self):
        channel = self.connection.channel()
        queue_setup(channel)
        self.channel = channel


# This file will get copied to the docker image's root folder(src) when being built
# When run, it attempts to create a pika.BlockingConnection() with the settings in CONSUMER_CONFIG
# There are 2 retry levels for the consumer. The first being in _create_connection() which is based on retry_limit
# The other being when the app crashes which is based on timeout
# If it fails, it will try to delete any existing connection and the reference to the instantiated class
# since the retry loop will recreate it
if __name__ == "__main__":

    start_timer = None
    current_timer = None

    while True:
        consumer = None
        try:
            consumer = RabbitMQConsumer(CONSUMER_CONFIG)
            if consumer.channel:
                consumer.channel.start_consuming()
        except Exception as e:
            del consumer
            if start_timer is None:
                start_timer = time()
                current_timer = 0
            else:
                current_timer = time() - start_timer
            if current_timer < CONSUMER_CONFIG["timeout"]:
                logging.warning(e, exc_info=True)
                logging.warning("Connection was closed. Retrying...")
                continue
            else:
                break
