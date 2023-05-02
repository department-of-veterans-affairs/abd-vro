import httpx
from hoppy import Service as HoppyService

RABBIT_MQ_CONFIG = {  # define this as a custom type
    "host": "localhost",
    "port": 5672,
    "username": "guest",
    "password": "guest",
    "queue_name": "hello",
    "exchange_name": "contention-classification-exchange",
    "service_queue_name": "domain-cc-classify",
    "retry_limit": 3,
    "timeout": 60 * 2,  # rename this to "timeout_seconds"
}
FAST_API_HOST = "http://localhost:18000"  # Note this will probably need to be pulled from an environment variable
http_client = httpx.Client()


# ambiguous types, see
# https://github.com/department-of-veterans-affairs/abd-vro/commit/260071d0a1f59ad0c44c78cb96dc2e511e59e3ee#diff-03e72a5bda60963cfbc4f88ab305c195a4f57d88ead1a0dafacdf2061c1cca41R9
def call_endpoint(message, routing_key):
    print(f"message: {message}")
    print(f"routing_key: {routing_key}")
    fastapi_url = f'{FAST_API_HOST}/{message["endpoint"]}'
    payload = message["payload"]
    fastapi_response = http_client.post(fastapi_url, data=payload)
    print(f"fastapi_response.json(): {fastapi_response.json()}")
    print(f"fastapi_response.status_code: {fastapi_response.status_code}")
    return {
        "status_code": fastapi_response.status_code,
        "response_body": fastapi_response.json(),
    }


def main():
    print("initializing Hoppy thing")
    rabbitmq_client = HoppyService(
        config=RABBIT_MQ_CONFIG,
        exchange=RABBIT_MQ_CONFIG["exchange_name"],
        consumers={RABBIT_MQ_CONFIG["service_queue_name"]: call_endpoint},
    )
    print("run()ing it")
    rabbitmq_client.run()


if __name__ == "__main__":
    main()
