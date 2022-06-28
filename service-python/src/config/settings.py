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

available_templates = {
    "hypertension": "hypertension",
    "asthma": "asthma"
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
