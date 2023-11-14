import os

RABBITMQ_CONFIG = {
    "host": os.environ.get("RABBITMQ_PLACEHOLDERS_HOST") or "localhost",
    "username": os.environ.get("RABBITMQ_USERNAME") or "user",
    "password": os.environ.get("RABBITMQ_PASSWORD") or "bitnami",
    "port": int(os.environ.get("RABBITMQ_PORT") or 5672),
    "retry_limit": int(os.environ.get("RABBITMQ_RETRY_LIMIT") or 3),
    "timeout": int(os.environ.get("RABBITMQ_TIMEOUT") or 60 * 60 * 3),  # 3 hours
    "initial_reconnect_delay": int(os.environ.get("RABBITMQ_INITIAL_RECONNECT_DELAY") or 0),
    "max_reconnect_delay": int(os.environ.get("RABBITMQ_MAX_RECONNECT_DELAY") or 30)
}
