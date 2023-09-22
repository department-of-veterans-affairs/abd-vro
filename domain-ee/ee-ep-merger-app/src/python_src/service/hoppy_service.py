from enum import Enum

from hoppy.async_hoppy_client import RetryableAsyncHoppyClient

APP_ID = "EP_MERGE"
EXCHANGE = "bipApiExchange"

config = {}


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
        client = RetryableAsyncHoppyClient(name=name.value,
                                           app_id=APP_ID,
                                           config=config,
                                           exchange=EXCHANGE,
                                           request_queue=queue,
                                           reply_to_queue=reply_queue)
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
