from unittest import mock
from unittest.mock import Mock

import pytest

import pika
from assessclaimdc7101.src import main_consumer as hypertension_consumer, logging_setup as hypertension_logging
from assessclaimdc6602.src import main_consumer as asthma_consumer, logging_setup as asthma_logging
from pdfgenerator.src import main_consumer as pdf_consumer, logging_setup as pdf_logging


# @pytest.mark.parametrize(
#     "consumer",
#     [
#         (
#             hypertension_consumer
#         ),
#         (
#             asthma_consumer
#         ),
#         (
#             pdf_consumer
#         )
#     ]
# )
# def test_main_consumer_construction(consumer):
#     """
#     Test the main consumer's ability to create a connection and setup queues
#     :param consumer: RabbitMQ consumer setup with pika
#     :return:
#     """
#
#     CONSUMER_TEST_CONFIG = {
#         "host": "test_host",
#         "username": "test_username",
#         "password": "test_password",
#         "port": 0000,
#         "retry_limit": 3,
#         "timeout": int(60 * 60 * 3)
#     }
#
#     with mock.patch.object(consumer.RabbitMQConsumer,
#                            '_create_connection') as _create_connection_mock:
#         test_consumer = consumer.RabbitMQConsumer(CONSUMER_TEST_CONFIG)
#
#     _create_connection_mock.assert_called_once_with('params', None)


@pytest.mark.parametrize(
    "logging_setup",
    [
        (
                hypertension_logging
        ),
        (
                asthma_logging
        ),
        (
                pdf_logging
        )
    ]
)
def test_logging_setup(logging_setup):
    with mock.patch.object(logging_setup, 'set_format') as mock_method:
        logger = logging_setup.set_format()

        mock_method.assert_called_once_with()

    assert logger
