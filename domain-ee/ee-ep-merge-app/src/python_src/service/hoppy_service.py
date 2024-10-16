import asyncio

from config import CLIENTS, ClientName, config
from hoppy.async_hoppy_client import AsyncHoppyClient, RetryableAsyncHoppyClient
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


class HoppyService:
    clients: dict[ClientName, AsyncHoppyClient] = {}

    def __init__(self) -> None:
        self.create_client(ClientName.GET_CLAIM)
        self.create_client(ClientName.GET_SPECIAL_ISSUE_TYPES)
        self.create_client(ClientName.GET_CLAIM_CONTENTIONS)
        self.create_client(ClientName.PUT_TSOJ)
        self.create_client(ClientName.CREATE_CLAIM_CONTENTIONS)
        self.create_client(ClientName.UPDATE_CLAIM_CONTENTIONS)
        self.create_client(ClientName.CANCEL_CLAIM)
        self.create_client(ClientName.BGS_ADD_CLAIM_NOTE)

    def create_client(self, name: ClientName) -> None:
        client_queue = CLIENTS[name]
        exchange_props = ExchangeProperties(name=client_queue.exchange, passive_declare=True)
        request_queue_props = QueueProperties(name=client_queue.request_queue, passive_declare=True)
        reply_queue_props = QueueProperties(name=client_queue.response_queue, passive_declare=False)
        client = RetryableAsyncHoppyClient(
            name=name.value,
            app_id=config['app_id'],
            config=config,
            exchange_properties=exchange_props,
            request_queue_properties=request_queue_props,
            request_routing_key=client_queue.request_queue,
            reply_queue_properties=reply_queue_props,
            reply_routing_key=client_queue.response_queue,
            request_message_ttl=config['request_message_ttl'],
            response_max_latency=config['response_max_latency'],
            response_reject_and_requeue_attempts=config['response_delivery_attempts'],
            max_retries=config['request_retries'],
        )
        self.clients[name] = client

    def get_client(self, name: str) -> AsyncHoppyClient:
        return self.clients.get(name)

    async def start_hoppy_clients(self):
        loop = asyncio.get_event_loop()
        for client in self.clients.values():
            await client.start(loop)
            while not client.is_ready():
                await asyncio.sleep(0)

    async def stop_hoppy_clients(self):
        for client in self.clients.values():
            await client.stop()

    def is_ready(self) -> bool:
        return all(client.is_ready() for client in self.clients.values())


HOPPY = HoppyService()
