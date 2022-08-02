#!/usr/bin/env python
import json
import pika
import uuid
from dotenv import load_dotenv

load_dotenv()

# HOST = os.environ['RABBITMQ_HOST']
HOST = "localhost"
EXCHANGE_NAME = 'health-assess-exchange'
SERVICE_QUEUE_NAME = '7101'

example_decision_data = {
    "veteranIcn": "1234567890V123456",
    "date_of_claim": "2022-7-09",
    "evidence": {
        "bp_readings": [
            {
                "date": "2022-04-19",
                "practitioner": "DR. THOMAS359 REYNOLDS206 PHD",
                "organization": "LYONS VA MEDICAL CENTER",
                "systolic": {
                    "code": "8480-6",
                    "display": "Systolic blood pressure",
                    "unit": "mm[Hg]",
                    "value": 115.0
                },
                "diastolic": {
                    "code": "8462-4",
                    "display": "Diastolic blood pressure",
                    "unit": "mm[Hg]",
                    "value": 87.0
                }
            }
        ],
        "medications": [
            {
                "status": "active",
                "authoredOn": "2013-04-15T01:15:52Z",
                "description": "Hydrochlorothiazide 25 MG",
                "notes": [
                    "Hydrochlorothiazide 25 MG"
                ],
                "dosageInstructions": [
                    "Once per day.",
                    "As directed by physician."
                ],
                "route": "As directed by physician.",
                "refills": "null",
                "duration": ""
            },
            {
                "status": "active",
                "authoredOn": "2013-04-14T06:00:00Z",
                "description": "Hydrochlorothiazide 25 MG",
                "notes": [],
                "dosageInstructions": [],
                "route": "",
                "refills": 0,
                "duration": "30 days"
            }
        ]
    }
}


class BaseClient(object):

    def __init__(self):
        credentials = pika.PlainCredentials('guest', 'guest')
        parameters = pika.ConnectionParameters(HOST,
                                               5672,
                                               '/',
                                               credentials)
        self.connection = pika.BlockingConnection(parameters)

        self.channel = self.connection.channel()
        result = self.channel.queue_declare(queue='')
        self.callback_queue = result.method.queue


    def call(self, decision_data):
        self.response = None
        self.corr_id = str(uuid.uuid4())
        self.channel.basic_publish(
            exchange=EXCHANGE_NAME,
            routing_key=SERVICE_QUEUE_NAME,
            properties=pika.BasicProperties(
                            reply_to = self.callback_queue,
                            correlation_id=self.corr_id,
                            ),
            body=bytes(json.dumps(decision_data), 'utf-8'))
        while self.response is None:
            self.connection.process_data_events()
        return self.response


rpc = BaseClient()

print(" [x] Requesting")
response = rpc.call(example_decision_data)
print(" [.] Got %r" % response)
