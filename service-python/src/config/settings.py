import os

pdf_options = {
    "page-size": "Letter",
    "margin-top": "0.5in",
    "margin-right": "0.5in",
    "margin-bottom": "0.5in",
    "margin-left": "0.5in",
    "encoding": "UTF-8",
    "zoom": "1.1",
}

redis_config = {
    "host": os.environ.get("REDIS_PLACEHOLDERS_HOST", "localhost"),
    "port": 6379,
    "retry_limit": 3,
}

consumer_config = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "port": 5672,
    "exchange_name": "pdf_generator",
    "queue_name": "generate_pdf",
    "retry_limit": 3,
}

codes = {
  "6602": "asthma",
  "7701": "hypertension"
}

s3_config = {
    "access_key": os.environ.get("AWS_ACCESS_KEY_ID", "test"),
    "secret_access_key": os.environ.get("AWS_SECRET_ACCESS_KEY", "test"),
    "session_token": os.environ.get("AWS_SESSION_TOKEN", "test")
}

