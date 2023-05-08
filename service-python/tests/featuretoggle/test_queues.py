import logging
from unittest.mock import Mock

import pytest
from featuretoggle.src.lib import queues


@pytest.mark.parametrize(
    "queue, toggle_queue_name", [(queues, "feature-toggle-queue")]
)
def test_queue_setup(queue, toggle_queue_name, caplog):
    queue_name = f"{toggle_queue_name}"
    channel = Mock(autospec=True, create=True)
    with caplog.at_level(logging.INFO):
        queue.queue_setup(channel=channel)

    channel.exchange_declare.assert_called_with(
        exchange="feature-toggle-exchange",
        exchange_type="direct",
        durable=True,
        auto_delete=True,
    )

    channel.queue_declare.assert_called_with(queue=queue_name, durable=True, auto_delete=True)
    channel.queue_bind.assert_called_with(
        queue=queue_name, exchange="feature-toggle-exchange"
    )
    assert channel.basic_consume

    assert (
        f" [*] Waiting for data for queue: {queue_name}. To exit press CTRL+C"
        in caplog.text
    )
