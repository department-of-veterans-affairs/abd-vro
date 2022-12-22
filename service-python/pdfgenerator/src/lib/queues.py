import base64
import json
import logging

import pika

from .pdf_generator import PDFGenerator
from .redis_client import RedisClient
from .settings import codes as DIAGNOSTIC_CODE_MAPPING
from .settings import pdf_options, queue_config, redis_config

EXCHANGE = queue_config["exchange_name"]
GENERATE_QUEUE = queue_config["generate_queue_name"]
FETCH_QUEUE = queue_config["fetch_queue_name"]


def on_generate_callback(channel, method, properties, body):
    try:
        redis_client = RedisClient(redis_config)
        pdf_generator = PDFGenerator(pdf_options)

        # binding_key = method.routing_key
        message = json.loads(body)
        # logging.info(f" [x] {binding_key}: Received message: {message}")
        claim_id = message["claimSubmissionId"]
        message["veteran_info"] = message["veteranInfo"]
        message["evidence"]["service_locations"] = message["evidence"]["serviceLocations"]
        code = message["diagnosticCode"]
        diagnosis_name = DIAGNOSTIC_CODE_MAPPING[code]
        variables = pdf_generator.generate_template_variables(diagnosis_name, message)
        # logging.info(f"Variables: {variables}")
        template = pdf_generator.generate_template_file(diagnosis_name, variables)
        pdf = pdf_generator.generate_pdf_from_string(diagnosis_name, template, variables)
        redis_client.save_hash_data(f"{claim_id}-pdf", mapping={"contents": base64.b64encode(pdf).decode("ascii"), "diagnosis": diagnosis_name})
        logging.info("Saved PDF")
        response = {"claimSubmissionId": claim_id, "status": "COMPLETE"}
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"claimSubmissionId": claim_id, "status": "ERROR", "reason": str(e)}
    channel.basic_publish(exchange=EXCHANGE, routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))


def on_fetch_callback(channel, method, properties, body):
    try:
        redis_client = RedisClient(redis_config)
        binding_key = method.routing_key
        claim_id = str(body, 'UTF-8')
        logging.info(f" [x] {binding_key}: Received Claim Submission ID: {claim_id}")
        if redis_client.exists(f"{claim_id}-pdf"):
            pdf = redis_client.get_hash_data(f"{claim_id}-pdf", "contents")
            diagnosis_name = redis_client.get_hash_data(f"{claim_id}-pdf", "diagnosis")
            logging.info("Fetched PDF")
            response = {"claimSubmissionId": claim_id, "status": "COMPLETE", "diagnosis": str(diagnosis_name.decode("ascii")), "pdfData": str(pdf.decode("ascii"))}
        else:
            logging.info("Claim ID not found")
            response = {"claimSubmissionId": claim_id, "status": "NOT_FOUND", "diagnosis": "", "pdfData": ""}
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"claimSubmissionId": claim_id, "status": "ERROR", "diagnosis": "", "pdfData": "", "reason": str(e)}
    channel.basic_publish(exchange=EXCHANGE, routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))


def queue_setup(channel):
    channel.exchange_declare(exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True)
    # Generate PDF Queue
    channel.queue_declare(queue=GENERATE_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=GENERATE_QUEUE, exchange=EXCHANGE)
    channel.basic_consume(queue=GENERATE_QUEUE, on_message_callback=on_generate_callback, auto_ack=True)
    # Fetch PDF Queue
    channel.queue_declare(queue=FETCH_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=FETCH_QUEUE, exchange=EXCHANGE)
    channel.basic_consume(queue=FETCH_QUEUE, on_message_callback=on_fetch_callback, auto_ack=True)
    logging.info(f" [*] Waiting for data for queue: {GENERATE_QUEUE}. To exit press CTRL+C")
    logging.info(f" [*] Waiting for data for queue: {FETCH_QUEUE}. To exit press CTRL+C")
