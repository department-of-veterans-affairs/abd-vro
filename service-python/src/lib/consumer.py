from time import sleep
from lib.pdf_generator import PDFGenerator
from config.pdf import pdf_options

import pika
import sys
import json
import base64

class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()
		self.pdf_generator = PDFGenerator(pdf_options)


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
		message = json.loads(body)
		print(f" [x] {binding_key}: Received message: {message}")
		# print(f"reply_to: {properties.reply_to}")
		# print(f"correlation_id: {properties.correlation_id}")
		# print(f"Headers: {properties.headers}")
		pdf_type = message["pdf_type"]

		template = self.pdf_generator.generate_template_file(f"{pdf_type}", message)
		pdf = self.pdf_generator.generate_pdf_from_string(template)
		print("PDF Base 64:", base64.b64encode(pdf))
		# return {"pdf": base64.b64encode(pdf).decode("utf-8")}


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
