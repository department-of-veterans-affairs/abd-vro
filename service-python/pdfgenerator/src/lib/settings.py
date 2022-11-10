import os

pdf_options = {
    "page-size": "Letter",
    "margin-top": "0.5in",
    "margin-right": "0.5in",
    "margin-bottom": "0.5in",
    "margin-left": "0.5in",
    "encoding": "UTF-8",
    "zoom": "1.1",
    "print-media-type": None
}

redis_config = {
    "host": os.environ.get("REDIS_PLACEHOLDERS_HOST", "localhost"),
    "port": 6379,
    "password": os.environ.get("REDIS_PLACEHOLDERS_PASSWORD", "not-redis-password"),
    "retry_limit": 3,
    # 3 hours
    "expiration": 60 * 60 * 3
}

queue_config = {
    "exchange_name": "pdf-generator",
    "generate_queue_name": "generate-pdf",
    "fetch_queue_name": "fetch-pdf",
}

codes = {
  "6602": "asthma",
  "7101": "hypertension",
  "summary": "summary"
}
