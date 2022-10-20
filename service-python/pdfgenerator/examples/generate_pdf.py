import json
import logging

import pika

logging.basicConfig(level=logging.INFO)

EXCHANGE_NAME = "pdf_generator"
QUEUE_NAME = "generate_pdf"

connection = pika.BlockingConnection(pika.ConnectionParameters(
               "localhost"))
channel = connection.channel()

channel.queue_declare(queue=QUEUE_NAME, durable=True, auto_delete=True)

code = "7101"

payload = {
  "claimSubmissionId": "1",
  "diagnosticCode": code,
  "veteranInfo": json.load(open("./veteran_info.json")),
  "evidence": json.load(open(f"./evidence_{code}.json"))
}

channel.basic_publish(exchange=EXCHANGE_NAME,
                      routing_key=QUEUE_NAME,
                      body=json.dumps(payload))
logging.info(" [x] Sent PDF Payload: ", payload)

connection.close()
