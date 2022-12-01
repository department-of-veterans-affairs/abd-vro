import json
import logging
from datetime import datetime

import pika

from . import main
from .settings import queue_config

EXCHANGE = queue_config["exchange_name"]
SERVICE_QUEUE = queue_config["service_queue_name"]
SUFFICIENT_QUEUE = queue_config["sufficient_queue_name"]


def pdf_evidence_request_callback(channel, method, properties, body):

    binding_key = method.routing_key
    message = json.loads(body.decode("utf-8"))
    logging.info(f" [x] {binding_key}: Received message at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}.")

    try:
        response = main.assess_hypertension(message)
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"status": "ERROR", "evidence": {}, "evidenceSummary": {}}

    channel.basic_publish(
        exchange=EXCHANGE,
        routing_key=properties.reply_to,
        properties=pika.BasicProperties(correlation_id=properties.correlation_id),
        body=json.dumps(response),
    )
    logging.info(f" [x] {binding_key}: Message sent at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}.")


def sufficiency_request_callback(channel, method, properties, body):

    binding_key = method.routing_key
    message = json.loads(body.decode("utf-8"))
    logging.info(f" [x] {binding_key}: Received message at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}.")

    try:
        response = main.assess_sufficiency(message)
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"status": "ERROR", "evidence": {}, "evidenceSummary": {}}

    channel.basic_publish(
        exchange=EXCHANGE,
        routing_key=properties.reply_to,
        properties=pika.BasicProperties(correlation_id=properties.correlation_id),
        body=json.dumps(response),
    )
    logging.info(f" [x] {binding_key}: Message sent at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}.")


def queue_setup(channel):

    channel.exchange_declare(
        exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True
    )

    # Sufficiency test queue
    channel.queue_declare(queue=SUFFICIENT_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=SUFFICIENT_QUEUE, exchange=EXCHANGE)
    channel.basic_qos(prefetch_count=250)
    channel.basic_consume(
        queue=SUFFICIENT_QUEUE, on_message_callback=sufficiency_request_callback, auto_ack=True
    )

    # PDF Service queue
    channel.queue_declare(queue=SERVICE_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=SERVICE_QUEUE, exchange=EXCHANGE)
    channel.basic_qos(prefetch_count=250)
    channel.basic_consume(
        queue=SERVICE_QUEUE, on_message_callback=pdf_evidence_request_callback, auto_ack=True
    )

    logging.info(
        f" [*] Waiting for data for queue: {SERVICE_QUEUE}. To exit press CTRL+C"
    )
    logging.info(
        f" [*] Waiting for data for queue: {SUFFICIENT_QUEUE}. To exit press CTRL+C"
    )
