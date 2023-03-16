import logging
from unittest import mock

import logging_setup
import main_consumer


def test_main_consumer_construction():
    """
    Test the main consumer's ability to create a connection and setup queues
    """

    CONSUMER_TEST_CONFIG = {
        "host": "test_host",
        "username": "test_username",
        "password": "test_password",
        "port": 0000,
        "retry_limit": 3,
        "timeout": int(60 * 60 * 3)
    }

    with mock.patch.object(main_consumer.RabbitMQConsumer,
                           '_create_connection', autospec=True):
        test_consumer = main_consumer.RabbitMQConsumer(CONSUMER_TEST_CONFIG)

    assert test_consumer.config == CONSUMER_TEST_CONFIG
    assert test_consumer.channel.basic_consume()
    assert main_consumer.logger  # Ensure logging setup is called


def test_logging_setup():
    """
    Test that the script for setting up logging calls the method to get the logger and format date correctly
    """
    with mock.patch.object(logging, "getLogger", autospec=True) as mock_method:
        logging_setup.set_format()

        mock_method.assert_called()

        assert logging_setup.logging.Formatter.default_time_format == '%Y-%m-%d %H:%M:%S'
