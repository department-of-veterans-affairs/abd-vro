import pytest
import logging
from assessclaimdc6602.src.lib import queues as q6602
from assessclaimdc7101.src.lib import queues as q7101
from unittest.mock import Mock


@pytest.mark.parametrize(
    "queue, service_queue_name",
    [
        (
                q6602,
                "6602"
        ),
        (
                q7101,
                "7101"
         )
    ]
)
def test_queues(queue, service_queue_name, caplog):
    channel = Mock(autospec=True, create=True)
    with caplog.at_level(logging.INFO):
        queue.queue_setup(channel=channel)

    channel.exchange_declare.assert_called_with(
        exchange="health-assess-exchange", exchange_type="direct", durable=True, auto_delete=True)
    channel.queue_declare.assert_called_with(queue=service_queue_name)
    channel.queue_bind.assert_called_with(queue=service_queue_name, exchange="health-assess-exchange")
    assert channel.basic_consume

    assert f' [*] Waiting for data for queue: {service_queue_name}. To exit press CTRL+C' in caplog.text
