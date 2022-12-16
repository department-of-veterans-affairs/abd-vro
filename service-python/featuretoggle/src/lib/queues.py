import json
import logging
from datetime import datetime

import pytz

from . import main, utils
from .redis_client import RedisClient
from .settings import queue_config, redis_config

EXCHANGE = queue_config["exchange_name"]
TOGGLE_QUEUE = queue_config["toggle_queue_name"]


def feature_toggle_request_callback(channel, method, properties, body):

    redis_client = RedisClient(redis_config)
    binding_key = method.routing_key
    logging.info(f" [x] {binding_key}: Received message.")

    try:
        response = main.report_feature_toggles()
        redis_client.publish("features", items=utils.create_features_list())
        redis_client.publish("features_timestamp", pytz.utc.localize(datetime.now()))
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"status": "ERROR", "features": {}}

    channel.basic_publish(
        exchange=EXCHANGE,
        routing_key=properties.reply_to,
        body=json.dumps(response),
    )
    logging.info(f" [x] {binding_key}: Message sent.")


def queue_setup(channel):

    channel.exchange_declare(
        exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True
    )

    # Feature toggle queue
    channel.queue_declare(queue=TOGGLE_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=TOGGLE_QUEUE, exchange=EXCHANGE)
    channel.basic_qos(prefetch_count=250)
    channel.basic_consume(
        queue=TOGGLE_QUEUE, on_message_callback=feature_toggle_request_callback, auto_ack=True
    )

    logging.info(
        f" [*] Waiting for data for queue: {TOGGLE_QUEUE}. To exit press CTRL+C"
    )
