import pytest
import logging
from assessclaimdc6602.src.lib import queues as q1
from assessclaimdc7101.src.lib import queues as q2
from unittest.mock import Mock


@pytest.mark.parametrize(
    "queue, service_queue_name",
    [
        (
                q1,
                "6602"
        ),
        (
                q2,
                "7101"
         )
    ]
)
def test_queues(queue, service_queue_name, caplog):
    channel = Mock(autospec=True, create=True)
    with caplog.at_level(logging.INFO):
        queue.queue_setup(channel=channel)
    assert f' [*] Waiting for data for queue: {service_queue_name}. To exit press CTRL+C' in caplog.text
