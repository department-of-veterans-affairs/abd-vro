import logging
from unittest import mock

import pytest

from assessclaimdc6602.src import logging_setup as asthma_logging
from assessclaimdc6602.src import main_consumer as asthma_consumer
from assessclaimdc7101.src import logging_setup as hypertension_logging
from assessclaimdc7101.src import main_consumer as hypertension_consumer
from pdfgenerator.src import logging_setup as pdf_logging
from pdfgenerator.src import main_consumer as pdf_consumer


@pytest.mark.parametrize(
    "consumer",
    [
        (
                asthma_consumer
        ),
        (
                hypertension_consumer
        ),
        (
                pdf_consumer
        )
    ]
)
def test_main_consumer_construction(consumer):
    """
    Test the main consumer's ability to create a connection and setup queues
    :param consumer: RabbitMQ consumer setup with pika
    :return:
    """

    CONSUMER_TEST_CONFIG = {
        "host": "test_host",
        "username": "test_username",
        "password": "test_password",
        "port": 0000,
        "retry_limit": 3,
        "timeout": int(60 * 60 * 3)
    }

    with mock.patch.object(consumer.RabbitMQConsumer,
                           '_create_connection', autospec=True):
        test_consumer = consumer.RabbitMQConsumer(CONSUMER_TEST_CONFIG)

    assert test_consumer.config == CONSUMER_TEST_CONFIG
    assert test_consumer.channel.basic_consume()
    assert consumer.logger  # Ensure logging setup is called


@pytest.mark.parametrize(
    "logging_setup",
    [
        (
                asthma_logging
        ),
        (
                hypertension_logging
        ),
        (
                pdf_logging
        )
    ]
)
def test_logging_setup(logging_setup):
    """
    Test that the script for setting up logging calls the method to get the logger and format date correctly
    """
    with mock.patch.object(logging, "getLogger", autospec=True) as mock_method:
        logging_setup.set_format()

        mock_method.assert_called()

        assert logging_setup.logging.Formatter.default_time_format == '%Y-%m-%d %H:%M:%S'