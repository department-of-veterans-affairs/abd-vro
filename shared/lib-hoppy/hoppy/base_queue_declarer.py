from hoppy.base_exchange_declarer import BaseExchangeDeclarer
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


class BaseQueueDeclarer(BaseExchangeDeclarer):
    def __init__(self,
                 config: [dict | None] = None,
                 exchange_properties: ExchangeProperties = ExchangeProperties(),
                 queue_properties: QueueProperties = QueueProperties(),
                 routing_key: str = ''):
        super().__init__(config, exchange_properties)

        self._set_queue_properties(queue_properties)
        self.routing_key = routing_key

    def _set_queue_properties(self, queue_properties: QueueProperties):
        self.queue_name = queue_properties.name
        self.passive_declare_queue = queue_properties.passive_declare
        self.durable_queue = queue_properties.durable
        self.auto_delete_queue = queue_properties.auto_delete
        self.exclusive_queue = queue_properties.exclusive

    def _on_exchange_declare_ok(self, _unused_frame):
        super()._on_exchange_declare_ok(_unused_frame)
        self._setup_queue()

    def _setup_queue(self):
        self._debug('declaringQueue',
                    queue=self.queue_name,
                    passive_declare=self.passive_declare_queue,
                    durable=self.durable_queue,
                    exclusive=self.exclusive_queue,
                    auto_delete=self.auto_delete_exchange)
        self._channel.queue_declare(queue=self.queue_name,
                                    passive=self.passive_declare_queue,
                                    durable=self.durable_queue,
                                    exclusive=self.exclusive_queue,
                                    auto_delete=self.auto_delete_queue,
                                    callback=self._on_queue_declare_ok)

    def _on_queue_declare_ok(self, _unused_frame):
        self._debug('declaredQueue',
                    queue=self.queue_name,
                    passive_declare=self.passive_declare_queue,
                    durable=self.durable_queue,
                    exclusive=self.exclusive_queue,
                    auto_delete=self.auto_delete_exchange)
        self._debug('bindingQueue',
                    queue=self.queue_name,
                    exchange=self.exchange_name,
                    routing_key=self.routing_key)
        self._channel.queue_bind(self.queue_name,
                                 self.exchange_name,
                                 routing_key=self.routing_key,
                                 callback=self._on_bind_ok)

    def _on_bind_ok(self, _unused_frame):
        self._debug('boundQueue',
                    queue=self.queue_name,
                    exchange=self.exchange_name,
                    routing_key=self.routing_key)
