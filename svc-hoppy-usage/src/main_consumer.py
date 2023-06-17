#!/usr/bin/env python
import time

import hoppy

print(f"Imported hoppy with default RabbitMQ config {hoppy.config.RABBITMQ_CONFIG}", flush=True)


class ServiceUnavailableError(hoppy.ServiceError):
    ERROR_CODE = 503

    def __str__(self):
        return f"Custom exception with parametrized argument {self.args[0]}"


def handler_one(message, routing_key):
    print(f"Handling request with message {message} and routing key {routing_key}")
    return {
        "result": f"This is the result of handling {message}",
        "timestamp": time.time(),
        "header": {
            "statusMessage": "Success!"
        },
    }


def handler_two(message, routing_key):
    raise ServiceUnavailableError("no handler available for this queue")


hoppy.Service(
    config={"retry_limit": 5},
    exchange="sample-exchange",
    consumers={
        "queue-one": handler_one,
        "queue-two": handler_two,
    },
).run()
