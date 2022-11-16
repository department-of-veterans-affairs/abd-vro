import os

queue_config = {
    "exchange_name": "feature-toggle-exchange",
    "toggle_queue_name": "feature-toggle-queue"
}

redis_config = {
    "host": os.environ.get("REDIS_PLACEHOLDERS_HOST", "localhost"),
    "port": 6379,
    "password": os.environ.get("REDIS_PLACEHOLDERS_PASSWORD", "not-redis-password"),
    "retry_limit": 3,
    # 3 hours
    "expiration": 60 * 60 * 3
}
