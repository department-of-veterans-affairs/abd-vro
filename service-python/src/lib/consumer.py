from time import sleep
import pika
import sys

class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()


	def __del__(self):
		self.connection.close()


	def _create_connection(self):
		for i in range(self.config["retry_limit"]):
			try:
				parameters = pika.ConnectionParameters(host=self.config["host"],    
				port = self.config["port"])
				return pika.BlockingConnection(parameters)
			except:
				print(f"RabbitMQ Connection Failed. Retrying in 15s")
				sleep(15)


	def on_message_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		# $logger.debug " [x] #{queue_name}: Received body with size: #{body.size}"
		# $logger.debug "reply_to: #{properties.reply_to}"
		# $logger.debug "correlation_id: #{properties.correlation_id}"
		# $logger.debug "Headers: #{properties.headers}"
		# $logger.debug "delivery_info: #{delivery_info}"
		print(f"Received new message for - {binding_key}")


	def on_return_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		print(f"Returned message for - {channel}")


	def setup_queue(self, exchange_name, queue_name):
		channel = self.connection.channel()
		channel.exchange_declare(exchange=exchange_name, exchange_type="topic")
		# This method creates or checks a queue
		channel.queue_declare(queue=queue_name)
		channel.queue_bind(queue=queue_name, exchange=exchange_name)
		channel.add_on_return_callback(self.on_return_callback)
		channel.queue_bind(queue=queue_name, exchange=exchange_name)
		channel.basic_consume(queue=queue_name, on_message_callback=self.on_message_callback, auto_ack=True)
		self.channel = channel
		print(f" [*] Waiting for data for {queue_name}. To exit press CTRL+C")
