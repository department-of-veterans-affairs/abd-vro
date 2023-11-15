from hoppy.base_channel_opener import BaseChannelOpener
from hoppy.hoppy_properties import ExchangeProperties


class BaseExchangeDeclarer(BaseChannelOpener):

    def __init__(self, config: [dict | None] = None, exchange_properties: ExchangeProperties = ExchangeProperties()):
        super().__init__(config)

        self._set_exchange_properties(exchange_properties)

    def _set_exchange_properties(self, exchange_properties: ExchangeProperties):
        self.exchange_name = exchange_properties.name
        self.exchange_type = exchange_properties.type
        self.passive_declare_exchange = exchange_properties.passive_declare
        self.durable_exchange = exchange_properties.durable
        self.auto_delete_exchange = exchange_properties.auto_delete

    def _open_channel(self):
        self._debug('openingChannel')
        self._connection.channel(on_open_callback=self._on_channel_open)

    def _on_channel_open(self, channel):
        super()._on_channel_open(channel)
        self._setup_exchange()

    def _setup_exchange(self):
        self._debug('declaringExchange',
                    exchange=self.exchange_name,
                    type=self.exchange_type,
                    passive_declare=self.passive_declare_exchange,
                    durable=self.durable_exchange,
                    auto_delete=self.auto_delete_exchange)
        self._channel.exchange_declare(exchange=self.exchange_name,
                                       exchange_type=self.exchange_type,
                                       passive=self.passive_declare_exchange,
                                       durable=self.durable_exchange,
                                       auto_delete=self.auto_delete_exchange,
                                       callback=self._on_exchange_declare_ok)

    def _on_exchange_declare_ok(self, _unused_frame):
        self._debug('declaredExchange',
                    exchange=self.exchange_name,
                    type=self.exchange_type,
                    passive_declare=self.passive_declare_exchange,
                    durable=self.durable_exchange,
                    auto_delete=self.auto_delete_exchange)
