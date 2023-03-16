import hoppy
import logging_setup
from lib import queues
from lib.settings import queue_config

if __name__ == "__main__":
    logging_setup.set_format()
    hoppy.Service(
        exchange=queue_config["exchange_name"],
        consumers={
            queue_config["generate_queue_name"]: queues.generate_pdf,
            queue_config["fetch_queue_name"]: queues.fetch_pdf,
            queue_config["generate_fetch_queue_name"]: queues.generate_pdf,
        }
    ).run()
