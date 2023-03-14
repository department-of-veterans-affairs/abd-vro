import functools
import json
import logging
import time

import pika


class Service:
    def __init__(self, config, exchange, consumers):
        self.config = config
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
            QueueConsumer(name, callback).bind_to_channel(channel, self.exchange)
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
    def __init__(self, service, name, callback):
        self.service = service
        self.name = name
        self.callback = callback
        self.wrapped_callback = self._wrap_callback()

    def bind_to_channel(self, channel, exchange):
        channel.queue_declare(queue=self.name, durable=True, auto_delete=True)
        channel.queue_bind(queue=self.name, exchange=exchange)
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
            return self._error_response(e, "Request deserialization error", code=400)
        try:
            response = self.callback(message, method.routing_key)
            status = 200
            if isinstance(response, tuple) and len(response) == 2 and isinstance(response[1], int):
                response, status = response
            if not isinstance(response, dict):
                response = {"responseBody": response}
            response.setdefault("statusCode", status)
            return response
        except ServiceError as e:
            return self._error_response(e, "")
        except Exception as e:
            return self._error_response(e, "Unhandled error")

    def _error_response(self, exception, description, code=None):
        message = str(exception) or type(exception).__name__
        return {
            "statusCode": code or getattr(exception, "ERROR_CODE", 500),
            "statusMessage": f"{description}: {message}",
        }


class ServiceError(Exception):
    ERROR_CODE = 500
