from datetime import datetime
from io import BytesIO
from time import sleep
from lib.pdf_generator import PDFGenerator
from lib.s3_uploader import upload_file
from config.settings import pdf_options, codes

import pika
import json
import base64

class RabbitMQConsumer:

	def __init__(self, config):
		self.config = config
		self.connection = self._create_connection()
		self.pdf_generator = PDFGenerator(pdf_options)
		self.code_list = codes


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
		code = message["diagnosticCode"]
		diagnosis_type = self.code_list[code]

		template = self.pdf_generator.generate_template_file(diagnosis_type, message)
		pdf = self.pdf_generator.generate_pdf_from_string(template)
		pdf_obj = BytesIO(pdf)
		file_name = f"VAMC_{diagnosis_type.upper()}_Rapid_Decision_Evidence--{datetime.now().strftime('%Y%m%d')}.pdf"
		upload_file(file_name, "vro-efolder", pdf_obj)
		# {"pdf": base64.b64encode(pdf).decode("utf-8")}
		response = {
			"claimSubmissionId": message["claimSubmissionId"],
			"diagnosticCode": message["diagnosticCode"],
			"evidenceSummaryLink": f"https://vro-efolder.s3.amazonaws.com/{file_name}"
		}
		print(f"Resonse: {response}")


	def on_return_callback(self, channel, method, properties, body):
		binding_key = method.routing_key
		print(f"Returned message for - {channel}")


	def setup_queue(self, exchange_name, queue_name):
		channel = self.connection.channel()
		channel.exchange_declare(exchange=exchange_name, exchange_type="direct", durable=True, auto_delete=True)
		# This method creates or checks a queue
		channel.queue_declare(queue=queue_name)
		channel.queue_bind(queue=queue_name, exchange=exchange_name)
		channel.add_on_return_callback(self.on_return_callback)
		channel.queue_bind(queue=queue_name, exchange=exchange_name)
		channel.basic_consume(queue=queue_name, on_message_callback=self.on_message_callback, auto_ack=True)
		self.channel = channel
		print(f" [*] Waiting for data for queue: {queue_name}. To exit press CTRL+C")
