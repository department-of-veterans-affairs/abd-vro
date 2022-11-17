from featuretoggle.src.lib.redis_client import RedisClient

redis_config = {
    "host": "localhost",
    "port": 6379,
    "retry_limit": 1,
    "expiration": 60 * 60 * 3,  # 3 hours
}


def test_valid_redis_connection():
    redis = RedisClient(redis_config)
    assert redis
