from unittest.mock import Mock

import pytest
from pdfgenerator.src.lib import queues


@pytest.mark.parametrize(
    "service_queue_name", ["generate-fetch-pdf"]
)
def test_queue_setup(service_queue_name):
    """Test if the proper queue gets generated."""
    exchange_name = "pdf-generator"
    queue_name = service_queue_name
    channel = Mock(autospec=True, create=True)
    queues.queue_setup(channel=channel)

    channel.exchange_declare.assert_called_with(
        exchange=exchange_name,
        exchange_type="direct",
        durable=True,
        auto_delete=True,
    )

    channel.queue_declare.assert_called_with(queue=queue_name, durable=True, auto_delete=True)
    channel.queue_bind.assert_called_with(
        queue=queue_name, exchange=exchange_name
    )
    assert channel.basic_consume
