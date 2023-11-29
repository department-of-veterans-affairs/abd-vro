import asyncio

from config import EXCHANGE, QUEUES, REPLY_QUEUES, ClientName, config
from hoppy.async_hoppy_client import RetryableAsyncHoppyClient
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


class HoppyService:
    clients = {}

    exchange_props = ExchangeProperties(name=EXCHANGE,
                                        passive_declare=False)

    def __init__(self):
        self.create_client(ClientName.GET_CLAIM)
        self.create_client(ClientName.GET_CLAIM_CONTENTIONS)
        self.create_client(ClientName.PUT_TSOJ)
        self.create_client(ClientName.CREATE_CLAIM_CONTENTIONS)
        self.create_client(ClientName.CANCEL_CLAIM)

    def create_client(self, name):
        req_queue = QUEUES[name]
        reply_queue = REPLY_QUEUES[name]
        request_queue_props = QueueProperties(name=req_queue,
                                              passive_declare=False)
        reply_queue_props = QueueProperties(name=reply_queue,
                                            passive_declare=False)
        client = RetryableAsyncHoppyClient(name=name.value,
                                           app_id=config["app_id"],
                                           config=config,
                                           exchange_properties=self.exchange_props,
                                           request_queue_properties=request_queue_props,
                                           request_routing_key=req_queue,
                                           reply_queue_properties=reply_queue_props,
                                           reply_routing_key=reply_queue,
                                           max_latency=config["request_timeout"],
                                           max_retries=config["request_retries"],
                                           response_reject_and_requeue_attempts=config["response_delivery_attempts"])
        self.clients[name] = client

    def get_client(self, name):
        return self.clients.get(name)

    async def start_hoppy_clients(self, loop):
        for client in self.clients.values():
            await client.start(loop)
            publisher_connection = client.async_publisher._connection
            consumer_connection = client.async_consumer._connection
            while not publisher_connection.is_open or not consumer_connection.is_open:
                await asyncio.sleep(0)

    async def stop_hoppy_clients(self):
        for client in self.clients.values():
            await client.stop()


HOPPY = HoppyService()
