from pdfgenerator.src.lib.queues import pdf_options, queue_config, redis_config
from pdfgenerator.src.lib.settings import (codes, pdf_options, queue_config,
                                           redis_config)

valid_pdf = {
    "page-size": "",
    "margin-top": "",
    "margin-right": "",
    "margin-bottom": "",
    "margin-left": "",
    "encoding": "",
    "zoom": "",
    "print-media-type": "",
    "enable-local-file-access": "",
}

valid_redis = {
    "host": "",
    "port": "",
    "password": "",
    "retry_limit": "",
    "expiration": ""
}

valid_queue = {
    "exchange_name": "",
    "generate_queue_name": "",
    "fetch_queue_name": "",
}

def test_valid_pdf_options():
  assert all(key in valid_pdf.keys() for key in pdf_options.keys())


def test_valid_redis_options():
  assert all(key in valid_redis.keys() for key in redis_config.keys())


def test_valid_queue_options():
  assert all(key in valid_queue.keys() for key in queue_config.keys())


def test_all_codes_are_numbers():
  for key in codes.keys():
    assert key.isnumeric() 