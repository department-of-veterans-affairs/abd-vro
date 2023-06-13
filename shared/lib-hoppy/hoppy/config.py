import os

RABBITMQ_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST", "localhost"),
    "username": os.environ.get("RABBITMQ_PLACEHOLDERS_USERNAME", "guest"),
    "password": os.environ.get("RABBITMQ_PLACEHOLDERS_USERPASSWORD", "guest"),
    "port": int(os.environ.get("RABBITMQ_PORT", 5672)),
    "retry_limit": int(os.environ.get("RABBITMQ_RETRY_LIMIT", 3)),
    "timeout": int(os.environ.get("RABBITMQ_TIMEOUT", 60 * 60 * 3)),  # 3 hours
}
