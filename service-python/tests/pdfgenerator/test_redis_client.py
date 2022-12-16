from unittest.mock import patch

from pdfgenerator.src.lib.redis_client import RedisClient
from pdfgenerator.src.lib.settings import redis_config


@patch("pdfgenerator.src.lib.redis_client.RedisClient")
def test_valid_redis_connection(redis_mock):
    """Test if a Redis Connection gets made."""
    redis = RedisClient(redis_config)
    assert redis


@patch("redis.Redis.set")
@patch("redis.Redis.expire")
@patch("redis.Redis.exists")
@patch("redis.Redis.get")
def test_redis_save_check_and_get_basic_key(redis_set, redis_expire, redis_exists, redis_get):
    """Test if Redis calls functions to save/get keys."""
    redis = RedisClient(redis_config)
    redis.save_data("test", "test")
    redis.exists("test")
    redis.get_data("test")
    assert redis_set.called
    assert redis_expire.called
    assert redis_exists.called
    assert redis_get.called


@patch("redis.Redis.hset")
@patch("redis.Redis.expire")
@patch("redis.Redis.hget")
def test_redis_save_check_and_get_basic_hash(redis_set, redis_expire, redis_get):
    """Test if Redis calls functions to save/get hashes."""
    redis = RedisClient(redis_config)
    redis.save_hash_data("test", mapping={"test": "test"})
    redis.get_hash_data("test", "test")
    assert redis_set.called
    assert redis_expire.called
    assert redis_get.called
