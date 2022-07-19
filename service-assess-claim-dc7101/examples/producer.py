#!/usr/bin/env python
import json
import pika
import uuid
from dotenv import load_dotenv

load_dotenv()

REPLY_QUEUE_NAME = 'example_assess'
# HOST = os.environ['RABBITMQ_HOST']
HOST = "localhost"
EXCHANGE_NAME = 'health-assess-exchange'
SERVICE_QUEUE_NAME = '7101'

example_decision_data = {
  "observation": 
    {"bp_readings":
        [
            {
                "diastolic": 115,
                "systolic": 180,
                "date": "2021-10-10"
            },
            {
                "diastolic": 110,
                "systolic": 200,
                "date": "2021-05-13"
            }
        ]
    },
  "medication": [
    {"text": "Benazepril",
    "code": "4492",
    "authored_on": "2022-04-01",
    "status": "active"},
    {"text": "Advil",
    "code": "1726319",
    "authored_on": "2022-04-01",
    "status": "active"}
  ],
  "date_of_claim": "2022-7-09",
  "vasrd": "7101",
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
        print(self.channel.channel_number)
        result = self.channel.queue_declare(queue=REPLY_QUEUE_NAME, exclusive=True)
        self.callback_queue = result.method.queue

        self.channel.basic_consume(
            queue=self.callback_queue,
            on_message_callback=self.on_response,
            auto_ack=True)

    def on_response(self, ch, method, props, body):
        if self.corr_id == props.correlation_id:
            self.response = body

    def call(self, decision_data):
        body = bytes(json.dumps(decision_data), 'utf-8')
        self.response = None
        self.corr_id = str(uuid.uuid4())
        self.channel.basic_publish(
            exchange=EXCHANGE_NAME,
            routing_key=decision_data["vasrd"],
            properties=pika.BasicProperties(
                reply_to=self.callback_queue,
                correlation_id=self.corr_id,
            ),
            body=body)
        while self.response is None:
            self.connection.process_data_events()
        return self.response


rpc = BaseClient()

print(" [x] Requesting")
response = rpc.call(example_decision_data)
print(" [.] Got %r" % response)

