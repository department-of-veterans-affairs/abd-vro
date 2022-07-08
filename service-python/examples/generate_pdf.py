import pika
import json
import logging

logging.basicConfig(level=logging.INFO)

EXCHANGE_NAME = "generate_pdf"
QUEUE_NAME = "pdf_generator"

connection = pika.BlockingConnection(pika.ConnectionParameters(
               "localhost"))
channel = connection.channel()

channel.queue_declare(queue=QUEUE_NAME)

code = 6602

payload = {
  "claimSubmissionId": 1,
  "diagnosticCode": code,
  "veteran_info": json.load(open("./veteran_info.json")),
  "evidence": json.load(open(f"./evidence_{code}.json"))
}

channel.basic_publish(exchange=EXCHANGE_NAME,
                      routing_key=QUEUE_NAME,
                      body=json.dumps(payload))
logging.info(" [x] Sent PDF Payload: ", payload)

connection.close()