import requests
import json
import pika

# Configurations for each environment
configs = {
    'dev': {
        'claims_url': 'https://claims-dev.bip.va.gov/api/v1',
        'rabbitmq_ip': '100.103.87.206',
        'rabbitmq_user': 'vro_dev_user',
        'rabbitmq_password': 'dev_password',
    },
    'qa': {
        'claims_url': 'https://claims-qa.bip.va.gov/api/v1',
        'rabbitmq_ip': '100.103.87.207',
        'rabbitmq_user': 'vro_qa_user',
        'rabbitmq_password': 'qa_password',
    },
    # Add other environments here...
}

# Select the environment
env = 'dev'  # Change this to select the environment
config = configs[env]

# Get JWT Token
url = f"{config['claims_url']}/token"
headers = {
    'accept': 'text/plain',
    'Content-Type': 'application/json',
}
data = {
  "userID": "BIPCLAIMSYSACCT",
  "applicationID": "BIPCLAIMSAPI",
  "stationID": "281"
}
response = requests.post(url, headers=headers, data=json.dumps(data))
jwt_token = response.text

# Get Claim ID
url = f"{config['claims_url']}/claims?include_closed=false&offset=0&limit=20&veteran_participant_id=100000043403"
headers = {
    'accept': 'application/json',
    'Authorization': f'Bearer {jwt_token}',
}
response = requests.get(url, headers=headers)
claims = response.json()

# Cancel Claim
claim_id = claims[0]['id']  # Assuming the first claim is the one to be cancelled
url = f"{config['claims_url']}/claims/{claim_id}/cancel"
headers = {
    'accept': 'application/json',
    'Authorization': f'Bearer {jwt_token}',
    'Content-Type': 'application/json',
}
data = {
  "lifecycleStatusReasonCode": "65",
  "closeReasonText": "string"
}
response = requests.put(url, headers=headers, data=json.dumps(data))

# RabbitMQ operations
rabbitmq_auth = pika.PlainCredentials(config['rabbitmq_user'], config['rabbitmq_password'])

# Connect to RabbitMQ
connection = pika.BlockingConnection(pika.ConnectionParameters(host=config['rabbitmq_ip'], credentials=rabbitmq_auth))
channel = connection.channel()

# Create reply queue
channel.queue_declare(queue='reply', durable=True)

# Publish payload
properties = pika.BasicProperties(
    delivery_mode=2,
    reply_to='reply',
    correlation_id=str(claim_id),
)
payload = json.dumps({
    "claimId": claim_id,
    "lifecycleStatusReasonCode": "65",
    "closeReasonText": f"cancel claim {claim_id}"
})
channel.basic_publish(
    exchange='bipApiExchange',
    routing_key='cancelClaimQueue',
    body=payload,
    properties=properties
)

connection.close()