from .pdf_generator import PDFGenerator
from .redis_client import RedisClient
from .settings import codes as DIAGNOSTIC_CODE_MAPPING, pdf_options, redis_config, queue_config

import pika
import json
import logging
import base64

EXCHANGE = queue_config["exchange_name"]
GENERATE_QUEUE = queue_config["generate_queue_name"]
FETCH_QUEUE = queue_config["fetch_queue_name"]


def on_generate_callback(channel, method, properties, body):
	try:
			redis_client = RedisClient(redis_config)
			pdf_generator = PDFGenerator(pdf_options)

			binding_key = method.routing_key
			message = json.loads(body)
			logging.info(f" [x] {binding_key}: Received message: {message}")
			claim_id = message["claimSubmissionId"]
			message["veteran_info"] = message["veteranInfo"]
			message["evidence"] = message["evidence"]
			code = message["diagnosticCode"]
			diagnosis_name = DIAGNOSTIC_CODE_MAPPING[code]
			variables = pdf_generator.generate_template_variables(diagnosis_name, message)
			logging.info(f"Variables: {variables}")
			template = pdf_generator.generate_template_file(diagnosis_name, variables)
			pdf = pdf_generator.generate_pdf_from_string(template)
			redis_client.save_data(claim_id, base64.b64encode(pdf).decode("ascii"))
			logging.info("Saved PDF")
			response = {"claimSubmissionId": claim_id, "status": "IN_PROGRESS"}
	except Exception as e:
			logging.error(e, exc_info=True)
			response = {"claimSubmissionId": claim_id, "status": "ERROR"}
	channel.basic_publish(exchange=EXCHANGE, routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))


def on_fetch_callback(channel, method, properties, body):
		try:
				redis_client = RedisClient(redis_config)

				binding_key = method.routing_key
				claim_id = str(body, 'UTF-8')
				logging.info(f" [x] {binding_key}: Received Claim Submission ID: {claim_id}")
				if redis_client.exists(claim_id):
						pdf = redis_client.get_data(claim_id)
						logging.info(f"Fetched PDF")
						response = {"claimSubmissionId": claim_id, "status": "COMPLETE", "pdfData": str(pdf.decode("ascii"))}
				else:
						logging.info(f"PDF still generating")
						response = {"claimSubmissionId": claim_id, "status": "IN_PROGRESS", "pdfData": ""}
		except:
				response = {"claimSubmissionId": claim_id, "status": "ERROR", "pdfData": ""}
		channel.basic_publish(exchange=EXCHANGE, routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))


def queue_setup(channel):
		channel.exchange_declare(exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True)
		# Generate PDF Queue
		channel.queue_declare(queue=GENERATE_QUEUE)
		channel.queue_bind(queue=GENERATE_QUEUE, exchange=EXCHANGE)
		channel.basic_consume(queue=GENERATE_QUEUE, on_message_callback=on_generate_callback, auto_ack=True)
		# Fetch PDF Queue
		channel.queue_declare(queue=FETCH_QUEUE)
		channel.queue_bind(queue=FETCH_QUEUE, exchange=EXCHANGE)
		channel.basic_consume(queue=FETCH_QUEUE, on_message_callback=on_fetch_callback, auto_ack=True)
		logging.info(f" [*] Waiting for data for queue: {GENERATE_QUEUE}. To exit press CTRL+C")
		logging.info(f" [*] Waiting for data for queue: {FETCH_QUEUE}. To exit press CTRL+C")
