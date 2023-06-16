import os

RABBITMQ_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST") or "localhost",
    "username": os.environ.get("RABBITMQ_PLACEHOLDERS_USERNAME") or "guest",
    "password": os.environ.get("RABBITMQ_PLACEHOLDERS_USERPASSWORD") or "guest",
    "port": int(os.environ.get("RABBITMQ_PORT") or 5672),
    "retry_limit": int(os.environ.get("RABBITMQ_RETRY_LIMIT") or 3),
    "timeout": int(os.environ.get("RABBITMQ_TIMEOUT") or 60 * 60 * 3),  # 3 hours
}
