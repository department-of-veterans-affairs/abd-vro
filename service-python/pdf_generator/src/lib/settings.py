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

queue_config = {
    "exchange_name": "pdf_generator",
    "generate_queue_name": "generate_pdf",
    "fetch_queue_name": "fetch_pdf",
}

codes = {
  "6602": "asthma",
  "7701": "hypertension"
}

