from pdfgenerator.src.lib.settings import (codes, pdf_options, queue_config,
                                           redis_config)

valid_pdf = {
    "encoding": "",
    "print-media-type": "",
    "enable-local-file-access": "",
    "disable-smart-shrinking": "",
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
    "generate_fetch_queue_name": "",
}


def test_valid_pdf_options():
    """Test if other PDF options are defined outside of the norm."""
    assert all(key in valid_pdf.keys() for key in pdf_options.keys())


def test_valid_redis_options():
    """Test if other Redis options are defined outside of the norm."""
    assert all(key in valid_redis.keys() for key in redis_config.keys())


def test_valid_queue_options():
    """Test if other queue options are defined outside of the norm."""
    assert all(key in valid_queue.keys() for key in queue_config.keys())


def test_all_codes_are_numbers():
    """Test if all diagnostic codes are numbers."""
    for key in codes.keys():
        assert key.isnumeric()
