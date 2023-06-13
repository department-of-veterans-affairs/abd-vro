import functools
import json
import logging
import os
import time

import pika

RABBITMQ_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "username": os.environ.get("RABBITMQ_PLACEHOLDERS_USERNAME", "guest"),
    "password": os.environ.get("RABBITMQ_PLACEHOLDERS_USERPASSWORD", "guest"),
    "port": int(os.environ.get("RABBITMQ_PORT", 5672)),
    "retry_limit": int(os.environ.get("RABBITMQ_RETRY_LIMIT", 3)),
    "timeout": int(os.environ.get("RABBITMQ_TIMEOUT", 60 * 60 * 3)),  # 3 hours
}


class Service:
    def __init__(self, exchange, consumers, config={}):
        self.config = {**RABBITMQ_CONFIG, **config}
        self.exchange = exchange
        self.consumers = consumers

        credentials = pika.PlainCredentials(config["username"], config["password"])
        self.params = pika.ConnectionParameters(config["host"], config["port"], credentials=credentials)

    def _connect(self):
        for i in range(self.config["retry_limit"]):
            try:
                return pika.BlockingConnection(self.params)
            except Exception as e:
                logging.warning(e, exc_info=True)
                logging.warning(f"RabbitMQ Connection Failed. Retrying in 30s ({i + 1}/{self.config['retry_limit']})")
                time.sleep(30)
        return None

    def _setup_channel(self, connection):
        channel = connection.channel()
        channel.exchange_declare(exchange=self.exchange, exchange_type="direct", durable=True, auto_delete=True)
        for name, callback in self.consumers.items():
            QueueConsumer(name, self.exchange, callback).bind_to_channel(channel)
            logging.info(f" [*] Waiting for data for queue: {name}. To exit press CTRL+C")
        return channel

    # When run, it attempts to create a pika.BlockingConnection() with the settings in CONSUMER_CONFIG
    # There are 2 retry levels for the consumer. The first being in _create_connection() which is based on retry_limit
    # The other being when the app crashes which is based on timeout
    # If it fails, it will try to delete any existing connection and the reference to the instantiated class
    # since the retry loop will recreate it
    def run(self):
        retry_time = None
        while True:
            connection = self._connect()
            if not connection:
                continue
            try:
                channel = self._setup_channel(connection)
                channel.start_consuming()
            except Exception as e:
                connection.close()
                if retry_time is None:
                    retry_time = time.time()
                elif time() - retry_time >= self.config["timeout"]:
                    break
                logging.warning(e, exc_info=True)
                logging.warning("Connection was closed. Retrying...")


class QueueConsumer:
    def __init__(self, name, exchange, callback):
        self.name = name
        self.exchange = exchange
        self.callback = callback
        self.wrapped_callback = self._wrap_callback()

    def bind_to_channel(self, channel):
        channel.queue_declare(queue=self.name, durable=True, auto_delete=True)
        channel.queue_bind(queue=self.name, exchange=self.exchange)
        channel.basic_consume(queue=self.name, on_message_callback=self.wrapped_callback, auto_ack=True)

    def _wrap_callback(self):
        @functools.wraps(self.callback)
        def wrapper(channel, method, properties, body):
            response = self._make_response(method, body)
            try:
                response_body = json.dumps(response)
            except Exception as e:
                response_body = json.dumps(self._error_response(e, "Response serialization error"))
            channel.basic_publish(
                exchange=self.exchange,
                routing_key=properties.reply_to,
                properties=pika.BasicProperties(correlation_id=properties.correlation_id),
                body=response_body,
            )
        return wrapper

    def _make_response(self, method, body):
        try:
            message = json.loads(body)
        except Exception as e:
            return self._error_response(e, "Request deserialization error", status_code=400)
        try:
            response = self.callback(message, method.routing_key)
            if not isinstance(response, dict):
                response = {"responseValue": response}
            response.setdefault("header", {})
            response["header"].setdefault("statusCode", 200)
            return response
        except ServiceError as e:
            return self._error_response(e, "")
        except Exception as e:
            return self._error_response(e, "Unhandled error")

    def _error_response(self, exception, description, status_code=None):
        message = str(exception) or type(exception).__name__
        return {
            "header": {
                "statusCode": status_code or getattr(exception, "ERROR_CODE", 500),
                "statusMessage": f"{description}: {message}",
            },
        }


class ServiceError(Exception):
    ERROR_CODE = 500
