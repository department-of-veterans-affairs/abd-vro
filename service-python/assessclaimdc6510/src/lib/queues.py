import json
import logging

import pika

from . import main
from .settings import queue_config

EXCHANGE = queue_config["exchange_name"]
SERVICE_QUEUE = queue_config["service_queue_name"]


def on_request_callback(channel, method, properties, body):
    binding_key = method.routing_key
    message = json.loads(body.decode("utf-8"))
    logging.info(f"claimSubmissionId: {message['claimSubmissionId']}, health data received by {binding_key} processor")
    try:
        response = main.assess_sinusitis(message)
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"evidence": None, "evidenceSummary": None, "errorMessage": str(e), "claimSubmissionId": message['claimSubmissionId']}

    channel.basic_publish(
        exchange=EXCHANGE,
        routing_key=properties.reply_to,
        properties=pika.BasicProperties(correlation_id=properties.correlation_id),
        body=json.dumps(response),
    )
    logging.info(f"claimSubmissionId: {response['claimSubmissionId']}, evaluation sent by {binding_key} processor")


def queue_setup(channel):
    channel.exchange_declare(
        exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True
    )
    channel.queue_declare(queue=SERVICE_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=SERVICE_QUEUE, exchange=EXCHANGE)

    channel.basic_qos(prefetch_count=250)
    channel.basic_consume(
        queue=SERVICE_QUEUE, on_message_callback=on_request_callback, auto_ack=True
    )
    logging.info(
        f" [*] Waiting for data for queue: {SERVICE_QUEUE}. To exit press CTRL+C"
    )
