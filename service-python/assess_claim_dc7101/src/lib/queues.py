from .settings import queue_config
from . import main

import pika
import json
import logging
import base64

EXCHANGE = queue_config["exchange_name"]
SERVICE_QUEUE = queue_config["service_queue_name"]
REPLY_QUEUE = queue_config["reply_queue_name"]


def rpc(body, route):
	logging.debug(f'CALL :{route}')
	hypertensionInput = json.loads(body.decode('utf-8'))
	hypertensionInput["date_of_claim"] = "2022-07-31"
	decision_response = main.assess_hypertension(hypertensionInput)
	return decision_response

def on_request_callback(channel, method, properties, body):
	logging.info("inside on_request_callback")
	route = method.routing_key
	response = rpc(body, route)
	channel.basic_publish(exchange=EXCHANGE, routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))

def queue_setup(channel):
	channel.exchange_declare(exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True)
	channel.queue_declare(queue=SERVICE_QUEUE)
	channel.queue_bind(queue=SERVICE_QUEUE, exchange=EXCHANGE)
	channel.basic_qos(prefetch_count=1)
	channel.basic_consume(queue=SERVICE_QUEUE, on_message_callback=on_request_callback, auto_ack=True)
	logging.info(f" [*] Waiting for data for queue: {SERVICE_QUEUE}. To exit press CTRL+C")
