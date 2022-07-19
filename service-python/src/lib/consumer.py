from datetime import datetime
from io import BytesIO
from time import sleep
from lib.pdf_generator import PDFGenerator
from lib.redis_client import RedisClient
from lib.s3_uploader import upload_file
from config.settings import pdf_options, codes
from config.settings import codes, pdf_options, redis_config

import pika
import json
import logging
import redis
import base64

logging.basicConfig(level=logging.INFO)

class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()
		self.redis_client = RedisClient(redis_config)
		self.pdf_generator = PDFGenerator(pdf_options)
		self.code_list = codes


	def __del__(self):
		self.connection.close()


	def _create_connection(self):
		for i in range(self.config["retry_limit"]):
			try:
				parameters = pika.ConnectionParameters(host=self.config["host"], port = self.config["port"])
				return pika.BlockingConnection(parameters)
			except:
				logging.warn(f"RabbitMQ Connection Failed. Retrying in 15s")
				sleep(15)


	def on_message_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		message = json.loads(body)
		logging.info(f" [x] {binding_key}: Received message: {message}")
		claim_id = message["claimSubmissionId"]
		if self.redis_client.exists(claim_id):
			logging.info(f"Fetched PDF: {self.redis_client.get_data(claim_id)}")
		else:
			code = message["diagnosticCode"]
			diagnosis_name = self.code_list[code]
			variables = self.pdf_generator.generate_template_variables(diagnosis_name, message)
			template = self.pdf_generator.generate_template_file(diagnosis_name, variables)
			pdf = self.pdf_generator.generate_pdf_from_string(template)
			self.redis_client.save_data(claim_id, base64.b64encode(pdf))
			logging.info("Saved PDF")
			response = {"file": None}
			channel.basic_publish(exchange=self.config["exchange_name"], routing_key=properties.reply_to, body=json.dumps(response))


	def on_return_callback(self, channel, method, properties, body):
		logging.info(f"Returned message for - {channel}")


	def setup_queue(self, exchange_name, queue_name):
		channel = self.connection.channel()
		channel.exchange_declare(exchange=exchange_name, exchange_type="direct", durable=True, auto_delete=True)
		channel.queue_declare(queue=queue_name)
		channel.queue_bind(queue=queue_name, exchange=exchange_name)
		channel.add_on_return_callback(self.on_return_callback)
		channel.queue_bind(queue=queue_name, exchange=exchange_name)
		channel.basic_consume(queue=queue_name, on_message_callback=self.on_message_callback, auto_ack=True)
		self.channel = channel
		logging.info(f" [*] Waiting for data for queue: {queue_name}. To exit press CTRL+C")
