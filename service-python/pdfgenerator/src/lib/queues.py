import base64
import logging

from .pdf_generator import PDFGenerator
from .redis_client import RedisClient
from .settings import codes, pdf_options, redis_config


def generate_pdf(message, routing_key):
    try:
        redis_client = RedisClient(redis_config)
        pdf_generator = PDFGenerator(pdf_options)

        # logging.info(f" Received message: {message}")
        claim_id = message["claimSubmissionId"]
        diagnosis_code = message["diagnosticCode"]
        message["veteran_info"] = message["veteranInfo"]
        pdf_template = message['pdfTemplate']
        template_name = codes[diagnosis_code] + "-" + pdf_template
        variables = pdf_generator.generate_template_variables(template_name, message)
        # logging.info(f"Variables: {variables}")
        template = pdf_generator.generate_template_file(template_name, variables)
        pdf = pdf_generator.generate_pdf_from_string(template_name, template, variables)
        redis_client.save_hash_data(f"{claim_id}-pdf", mapping={"contents": base64.b64encode(pdf).decode("ascii"), "diagnosis": codes[diagnosis_code]})
        logging.info(f"Claim {claim_id}: Saved PDF")
        # Check if the routing key is for a generate or generate and fetch
        if routing_key == "generate-pdf":
            response = {"claimSubmissionId": claim_id, "status": "COMPLETE"}
        else:
            pdf = redis_client.get_hash_data(f"{claim_id}-pdf", "contents")
            diagnosis_name = redis_client.get_hash_data(f"{claim_id}-pdf", "diagnosis")
            logging.info("Fetched PDF")
            response = {"claimSubmissionId": claim_id, "status": "COMPLETE", "diagnosis": str(diagnosis_name.decode("ascii")), "pdfData": str(pdf.decode("ascii"))}
    except Exception as e:
        logging.error(e, exc_info=True)
        response = {"claimSubmissionId": claim_id, "status": "ERROR", "reason": str(e)}
    return response


def on_fetch_callback(message, routing_key):
    try:
        redis_client = RedisClient(redis_config)
        claim_id = message
        logging.info(f" [x] {routing_key}: Received Claim Submission ID: {claim_id}")
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
    return response
