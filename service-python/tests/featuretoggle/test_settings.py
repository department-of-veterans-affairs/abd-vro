from featuretoggle.src.lib.settings import queue_config, redis_config

valid_redis = {
    "host": "",
    "port": "",
    "password": "",
    "retry_limit": "",
    "expiration": ""
}

valid_queue = {
    "exchange_name": "",
    "toggle_queue_name": "",
}


def test_valid_redis_options():
    assert all(key in valid_redis.keys() for key in redis_config.keys())


def test_valid_queue_options():
    assert all(key in valid_queue.keys() for key in queue_config.keys())
