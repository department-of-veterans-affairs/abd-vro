from hoppy.hoppy_properties import ExchangeProperties, QueueProperties

EXCHANGE = "xample-async-client-exchange"
REQUEST_QUEUE = "xample-async-client-request"
REPLY_QUEUE = "xample-async-client-reply"

exchange_properties = ExchangeProperties(name=EXCHANGE, type="direct", passive_declare=False, durable=False, auto_delete=True)
request_queue_properties = QueueProperties(name=REQUEST_QUEUE, passive_declare=False, durable=False, auto_delete=True, exclusive=False)
reply_queue_properties = QueueProperties(name=REPLY_QUEUE, passive_declare=False, durable=False, auto_delete=True, exclusive=False)
