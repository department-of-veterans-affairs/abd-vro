import os

import hoppy
import logging_setup
from lib import queues
from lib.settings import queue_config

logging_setup.set_format()

CONSUMER_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "username": os.environ.get("RABBITMQ_PLACEHOLDERS_USERNAME", "guest"),
    "password": os.environ.get("RABBITMQ_PLACEHOLDERS_USERPASSWORD", "guest"),
    "port": int(os.environ.get("RABBITMQ_PORT", 5672)),
    "retry_limit": int(os.environ.get("RABBITMQ_RETRY_LIMIT", 3)),
    # 3 hours
    "timeout": int(os.environ.get("RABBITMQ_TIMEOUT", 60 * 60 * 3))
}


if __name__ == "__main__":
    hoppy.Service(
        config=CONSUMER_CONFIG,
        exchange=queue_config["exchange_name"],
        consumers={
            queue_config["generate_queue_name"]: queues.generate_pdf,
            queue_config["fetch_queue_name"]: queues.fetch_pdf,
            queue_config["generate_fetch_queue_name"]: queues.generate_pdf,
        }
    ).run()
