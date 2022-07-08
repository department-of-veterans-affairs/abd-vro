import os

pdf_options = {
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
