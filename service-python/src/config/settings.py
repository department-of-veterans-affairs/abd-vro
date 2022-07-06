import os

pdf_options = {
    # "dpi": 300,
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
    "binding_key": "test",
    "retry_limit": 3,
    "save_pdf": True
}

s3_config = {
    "access_key": os.environ.get("AWS_ACCESS_KEY_ID", "test"),
    "secret_access_key": os.environ.get("AWS_SECRET_ACCESS_KEY", "test"),
    "session_token": os.environ.get("AWS_SESSION_TOKEN", "test")
}

codes = {
  6602: "asthma",
  7701: "hypertension"
}