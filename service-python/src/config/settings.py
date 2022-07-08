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

consumer_config = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "port": 5672,
    "exchange": "generate_pdf",
    "queue_name": "pdf_generator",
    "retry_limit": 3,
}

codes = {
  6602: "asthma",
}