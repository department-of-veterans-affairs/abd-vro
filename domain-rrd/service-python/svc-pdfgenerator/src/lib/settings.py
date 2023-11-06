import os

pdf_options = {
    "encoding": "UTF-8",
    "print-media-type": None,
    "enable-local-file-access": None,
    "disable-smart-shrinking": None,
}

redis_config = {
    "host": os.environ.get("REDIS_PLACEHOLDERS_HOST", "localhost"),
    "port": 6379,
    "password": os.environ.get("REDIS_PASSWORD", "not-redis-password"),
    "retry_limit": 3,
    # 3 hours
    "expiration": 60 * 60 * 3
}

queue_config = {
    "exchange_name": "pdf-generator",
    "generate_queue_name": "generate-pdf",
    "fetch_queue_name": "fetch-pdf",
    "generate_fetch_queue_name": "generate-fetch-pdf",
}

codes = {
  "6602": "asthma",
  "7101": "hypertension",
}
