import pika
import sys

class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()

	def __del__(self):
		self.connection.close()

	def _create_connection(self):
		parameters = pika.ConnectionParameters(host=self.config["host"],    
		port = self.config["port"])
		return pika.BlockingConnection(parameters)

	def on_message_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		print(f"Received new message for - {binding_key}")
			
	def setup(self):
		channel = self.connection.channel()
		channel.exchange_declare(exchange=self.config["exchange"],
		exchange_type="topic")
		# This method creates or checks a queue
		channel.queue_declare(queue=self.config["queue_name"])
		# Binds the queue to the specified exchang
		channel.queue_bind(queue=self.config["queue_name"], exchange=self.config["exchange"], routing_key=self.config["binding_key"])
		channel.basic_consume(queue=self.config["queue_name"], on_message_callback=self.on_message_callback, auto_ack=True)
		print(f" [*] Waiting for data for {self.config['queue_name']}. To exit press CTRL+C")
		try:
			channel.start_consuming()
		except KeyboardInterrupt:
			channel.stop_consuming()

# class RabbitMQConsumer

#   def initialize(bunny_args)
#     @connection = Bunny.new(bunny_args)
#     connect(@connection)
#     @exchanges = {}
#     @queues = {}
#   end

#   def connect(connection)
#     attempts ||= 1
#     connection.start
#   rescue => error
#     $logger.error error.message
#     if (attempts += 1) < 5
#       $logger.warn "Retrying to connect to RabbitMQ in 10 seconds"
#       sleep 10
#       retry
#     else
#       raise error
#     end
#   end

#   def close
#     @connection.close
#   end

#   CAMEL_MQ_PROPERTIES = { durable: true, auto_delete: true }

#   def setup_queue(exchange_name:, queue_name:)
#     channel = @connection.create_channel
#     # Exchange and queue properties must match those set up in Camel
#     # http://rubybunny.info/articles/exchanges.html
#     exchange = channel.direct(exchange_name, CAMEL_MQ_PROPERTIES)
#     @exchanges[exchange_name] = exchange
#     # in case message cannot be delivered
#     exchange.on_return do |return_info, properties, content|
#       $logger.info "Got a returned message: #{content}"
#     end

#     channel.queue(queue_name, CAMEL_MQ_PROPERTIES).tap do |queue|
#       @queues[queue_name] = queue
#       # https://www.baeldung.com/java-rabbitmq-exchanges-queues-bindings
#       queue.bind(exchange_name, routing_key: queue_name)
#     end
#   end

#   def subscribe_to(exchange_name, queue_name)
#     begin
#       queue = @queues[queue_name]
#       $logger.info " [*] Waiting for messages for queue #{queue_name}. To exit press CTRL+C"
#       queue.subscribe do |delivery_info, properties, body|
#         $logger.debug " [x] #{queue_name}: Received body with size: #{body.size}"
#         $logger.debug "reply_to: #{properties.reply_to}"
#         $logger.debug "correlation_id: #{properties.correlation_id}"
#         $logger.debug "Headers: #{properties.headers}"
#         $logger.debug "delivery_info: #{delivery_info}"
#         # delivery_info.routing_key is the same as queue_name in this case

#         json = JSON.parse(body)
#         response = yield(json)
#       rescue => e
#         $logger.error e.backtrace
#         response = {
#           error_message: e.message,
#           backtrace: e.backtrace.join("\n ")
#         }
#       ensure
#         $logger.info "Response: #{response}"
#         # delivery_info.exchange or exchange_name can be used
#         exchange = @exchanges[exchange_name]
#         exchange.publish(
#           response.to_json,
#           routing_key: properties.reply_to,
#           correlation_id: properties.correlation_id
#         )
#       end
#     rescue Interrupt => _
#       @connection.close
#       exit(0)
#     end
#   end
# end
