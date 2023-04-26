import base64
import json
import logging

import pika

from .pdf_generator import PDFGenerator
from .redis_client import RedisClient
from .settings import codes, pdf_options, queue_config, redis_config

EXCHANGE = queue_config["exchange_name"]
GENERATE_QUEUE = queue_config["generate_queue_name"]
FETCH_QUEUE = queue_config["fetch_queue_name"]
GENERATE_FETCH_QUEUE = queue_config["generate_fetch_queue_name"]


def on_generate_callback(channel, method, properties, body):
    """Gets called when messages arrive in the GENERATE_QUEUE
    :param channel: Object to make further modifications like adding queues, publishing, etc.
    :type channel: pika.channel.Channel
    :param method: Details on how the data was passed to the queue
    :type method: pika.spec.Basic.Deliver
    :param properties: Additional data used for replying and correlating messages
    :type properties: pika.spec.BasicProperties
    :param body: Request data that's passed to the PDF for generation
    :type body: bytes
    """

    try:
        message = json.loads(body)
        redis_client = RedisClient(redis_config)
        pdf_generator = PDFGenerator(pdf_options, message)
        claim_id = message["claimSubmissionId"]
        diagnosis_code = message["diagnosticCode"]
        message["veteran_info"] = message["veteranInfo"]
        pdf_template = message['pdfTemplate']
        try:
            template_name = codes[diagnosis_code] + "-" + pdf_template
            diagnosis_name = codes[diagnosis_code]
        except KeyError:
            template_name = "default"
            diagnosis_name = "default"
        variables = pdf_generator.generate_template_variables(template_name, message)
        template = pdf_generator.generate_template_file(template_name, variables)
        pdf = pdf_generator.generate_pdf_from_string(template_name, template, variables)
        redis_client.save_hash_data(f"{claim_id}-pdf", mapping={"contents": base64.b64encode(pdf).decode("ascii"), "diagnosis": diagnosis_name})
        logging.info(f"Claim {claim_id}: Saved PDF")
        # Check if the routing key is for a generate or generate and fetch
        if method.routing_key == "generate-pdf":
            response = {"claimSubmissionId": claim_id, "status": "COMPLETE"}
        else:
            pdf = redis_client.get_hash_data(f"{claim_id}-pdf", "contents")
            diagnosis_name = redis_client.get_hash_data(f"{claim_id}-pdf", "diagnosis")
            logging.info("Fetched PDF")
            response = {"claimSubmissionId": claim_id, "status": "COMPLETE", "diagnosis": str(diagnosis_name.decode("ascii")), "pdfData": str(pdf.decode("ascii"))}
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"claimSubmissionId": claim_id, "status": "ERROR", "reason": str(e)}
    channel.basic_publish(exchange=EXCHANGE, routing_key=properties.reply_to, properties=pika.BasicProperties(correlation_id=properties.correlation_id), body=json.dumps(response))


def on_fetch_callback(channel, method, properties, body):
    """Gets called when messages arrive in the FETCH_QUEUE
    :param channel: Object to make further modifications like adding queues, publishing, etc.
    :type channel: pika.channel.Channel
    :param method: Details on how the data was passed to the queue
    :type method: pika.spec.Basic.Deliver
    :param properties: Additional data used for replying and correlating messages
    :type properties: pika.spec.BasicProperties
    :param body: Request data that's passed to the PDF for generation
    :type body: bytes
    """

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
    """Gets called wby the main_consumer to setup all the available queues and their callbacks
    :param channel: Object to make further modifications like adding queues, publishing, etc.
    :type channel: pika.channel.Channel
    """

    channel.exchange_declare(exchange=EXCHANGE, exchange_type="direct", durable=True, auto_delete=True)
    # Generate PDF Queue
    channel.queue_declare(queue=GENERATE_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=GENERATE_QUEUE, exchange=EXCHANGE)
    channel.basic_consume(queue=GENERATE_QUEUE, on_message_callback=on_generate_callback, auto_ack=True)
    # Fetch PDF Queue
    channel.queue_declare(queue=FETCH_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=FETCH_QUEUE, exchange=EXCHANGE)
    channel.basic_consume(queue=FETCH_QUEUE, on_message_callback=on_fetch_callback, auto_ack=True)
    # Generate Fetch PDF Queue
    channel.queue_declare(queue=GENERATE_FETCH_QUEUE, durable=True, auto_delete=True)
    channel.queue_bind(queue=GENERATE_FETCH_QUEUE, exchange=EXCHANGE)
    channel.basic_consume(queue=GENERATE_FETCH_QUEUE, on_message_callback=on_generate_callback, auto_ack=True)
    logging.info(f" [*] Waiting for data for queue: {GENERATE_QUEUE}. To exit press CTRL+C")
    logging.info(f" [*] Waiting for data for queue: {FETCH_QUEUE}. To exit press CTRL+C")
    logging.info(f" [*] Waiting for data for queue: {GENERATE_FETCH_QUEUE}. To exit press CTRL+C")
