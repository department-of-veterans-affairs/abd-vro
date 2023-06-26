#!/usr/bin/env python
import time

import hoppy

print(f"Imported hoppy with default RabbitMQ config {hoppy.config.RABBITMQ_CONFIG}", flush=True)


class ServiceUnavailableError(hoppy.ServiceError):
    ERROR_CODE = 503

    def __str__(self):
        return f"Custom exception with parametrized argument {self.args[0]!r}"


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
    raise ServiceUnavailableError("no handler for this queue")


def handler_reciprocal(message, routing_key):
    # a "working" arithmetic handler with opportunities for "unexpected" exceptions
    return {"value": 1 / message["value"]}


hoppy.Service(
    config={"retry_limit": 5},
    exchange="sample-exchange",
    consumers={
        "queue-one": handler_one,
        "queue-two": handler_two,
        "queue-reciprocal": handler_reciprocal,
    },
).run()
