import logging
import time

import pika
from hoppy.config import RABBITMQ_CONFIG
from hoppy_client import HoppyClient

EXCHANGE = "bipApiExchange"

connection_params = pika.ConnectionParameters(RABBITMQ_CONFIG["host"], RABBITMQ_CONFIG["port"],
                                              credentials=pika.PlainCredentials(RABBITMQ_CONFIG["username"],
                                                                                RABBITMQ_CONFIG["password"]))


def get_connection():
    retries = RABBITMQ_CONFIG["retry_limit"]
    for i in range(retries):
        try:
            return pika.BlockingConnection(connection_params)
        except Exception as e:
            logging.warning(e, exc_info=True)
            logging.warning(f"RabbitMQ Connection Failed. Retrying in 30s ({i + 1}/{retries})")
            time.sleep(30)
    return None


connection = get_connection()
connection.channel().exchange_declare(exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True)

set_temp_station_of_jurisdiction = HoppyClient(connection,
                                               EXCHANGE,
                                               "putTemporaryStationOfJurisdictionQueue",
                                               "putTemporaryStationOfJurisdictionResponseQueue")
get_claim_contentions = HoppyClient(connection,
                                    EXCHANGE,
                                    "getClaimContentionsQueue",
                                    "getClaimContentionsResponseQueue")
update_contentions = HoppyClient(connection,
                                 EXCHANGE,
                                 "updateClaimContentionQueue",
                                 "updateClaimContentionResponseQueue")
cancel_claim = HoppyClient(connection,
                           EXCHANGE,
                           "cancelClaimQueue",
                           "cancelClaimResponseQueue")
