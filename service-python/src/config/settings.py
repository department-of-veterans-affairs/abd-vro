pdf_options = {
    "dpi": 300,
    "page-size": "Letter",
    "margin-top": "0.25in",
    "margin-right": "0.25in",
    "margin-bottom": "0.25in",
    "margin-left": "0.25in",
    "encoding": "UTF-8",
    "zoom": "0.8"
}

available_templates = {
    "hypertension": "hypertension",
    "cancer": "pact_data_sheet"
}

consumer_config = {
    "host": "localhost",
    "port": 5672,
    "exchange": "generate_pdf",
    "queue_name": "pdf_generator",
    "binding_key": "test",
    "retry_limit": 3,
    "save_pdf": True
}