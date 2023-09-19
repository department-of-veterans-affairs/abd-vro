from enum import Enum

import pika
from async_hoppy_client import AsyncHoppyClient
from hoppy.config import RABBITMQ_CONFIG

EXCHANGE = "bipApiExchange"

connection_params = pika.ConnectionParameters(RABBITMQ_CONFIG["host"], RABBITMQ_CONFIG["port"],
                                              credentials=pika.PlainCredentials(RABBITMQ_CONFIG["username"],
                                                                                RABBITMQ_CONFIG["password"]))


class HoppyService:
    clients = {}

    def __init__(self):
        self.create_client(HoppyClientName.PUT_TSOJ,
                           "putTemporaryStationOfJurisdictionQueue",
                           "putTemporaryStationOfJurisdictionResponseQueue")
        self.create_client(HoppyClientName.GET_CLAIM_CONTENTIONS,
                           "getClaimContentionsQueue",
                           "getClaimContentionsResponseQueue")
        self.create_client(HoppyClientName.UPDATE_CLAIM_CONTENTIONS,
                           "updateClaimContentionQueue",
                           "updateClaimContentionResponseQueue")
        self.create_client(HoppyClientName.CANCEL_CLAIM,
                           "cancelClaimQueue",
                           "cancelClaimResponseQueue")

    def create_client(self, name, queue, reply_queue):
        client = AsyncHoppyClient(name.value,
                                  connection_params,
                                  EXCHANGE,
                                  queue,
                                  reply_queue)
        self.clients[name] = client

    def get_client(self, name):
        return self.clients.get(name)

    def start_hoppy_clients(self, loop):
        for client in self.clients.values():
            client.start(loop)

    def stop_hoppy_clients(self):
        for client in self.clients.values():
            client.stop()


class HoppyClientName(str, Enum):
    PUT_TSOJ = "putTemporaryStationOfJurisdictionClient"
    GET_CLAIM_CONTENTIONS = "getClaimContentionsClient"
    UPDATE_CLAIM_CONTENTIONS = "updateClaimContentionsClient"
    CANCEL_CLAIM = "cancelClaimClient"
