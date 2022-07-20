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
		self.setup_queues()
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


	def on_generate_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		message = json.loads(body)
		logging.info(f" [x] {binding_key}: Received message: {message}")
		claim_id = message["claimSubmissionId"]
		message["veteran_info"] = message["veteranInfo"]
		code = message["diagnosticCode"]
		diagnosis_name = self.code_list[code]
		variables = self.pdf_generator.generate_template_variables(diagnosis_name, message)
		logging.info(f"Variables: {variables}")
		template = self.pdf_generator.generate_template_file(diagnosis_name, variables)
		pdf = self.pdf_generator.generate_pdf_from_string(template)
		self.redis_client.save_data(claim_id, base64.b64encode(pdf))
		logging.info("Saved PDF")
		response = {"claimSubmissionId": claim_id, "status": "IN_PROGRESS", "pdf": None}
		channel.basic_publish(exchange=self.config["exchange_name"], routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))

	
	def on_fetch_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		message = json.loads(body)
		logging.info(f" [x] {binding_key}: Received message: {message}")
		claim_id = message["claimSubmissionId"]
		if self.redis_client.exists(claim_id):
			pdf = self.redis_client.get_data(claim_id)
			logging.info(f"Fetched PDF")
			response = {"claimSubmissionId": claim_id, "status": "COMPLETE", "pdf": str(pdf)}
			response = str(pdf.decode("ascii"))
		else:
			logging.info(f"PDF still generating")
			response = {"claimSubmissionId": claim_id, "status": "IN_PROGRESS", "pdf": None}
		channel.basic_publish(exchange=self.config["exchange_name"], routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=response)


	def setup_queues(self):
		channel = self.connection.channel()
		channel.exchange_declare(exchange=self.config["exchange_name"], exchange_type="direct", durable=True, auto_delete=True)
		# Generate PDF Queue
		channel.queue_declare(queue=self.config["generate_queue_name"])
		channel.queue_bind(queue=self.config["generate_queue_name"], exchange=self.config["exchange_name"])
		channel.basic_consume(queue=self.config["generate_queue_name"], on_message_callback=self.on_generate_callback, auto_ack=True)
		# Fetch PDF Queue
		channel.queue_declare(queue=self.config["fetch_queue_name"])
		channel.queue_bind(queue=self.config["fetch_queue_name"], exchange=self.config["exchange_name"])
		channel.basic_consume(queue=self.config["fetch_queue_name"], on_message_callback=self.on_fetch_callback, auto_ack=True)
		self.channel = channel
		logging.info(f" [*] Waiting for data for queue: {self.config['generate_queue_name']}. To exit press CTRL+C")
		logging.info(f" [*] Waiting for data for queue: {self.config['fetch_queue_name']}. To exit press CTRL+C")
