import pika
import json

EXCHANGE_NAME = "generate_pdf"
QUEUE_NAME = "pdf_generator"

connection = pika.BlockingConnection(pika.ConnectionParameters(
               "localhost"))
channel = connection.channel()

channel.queue_declare(queue=QUEUE_NAME)

payload = {
  "contention": "asthma",
  "patient_info": json.load(open("./patient_info.json")),
  "assessed_data": json.load(open("./assessed_data_asthma.json"))
}

channel.basic_publish(exchange=EXCHANGE_NAME,
                      routing_key=QUEUE_NAME,
                      body=json.dumps(payload))
print(" [x] Sent PDF Payload: ", payload)

connection.close()