import json
import logging
from unittest.mock import Mock, patch

import pytest

from assessclaimdc6602.src.lib import queues as q6602
from assessclaimdc6602.src.lib.main import assess_asthma as main6602
from assessclaimdc7101.src.lib import queues as q7101


@pytest.mark.parametrize(
    "queue, service_queue_name", [(q6602, "6602"), (q7101, "7101")]
)
def test_queue_setup(queue, service_queue_name, caplog):
    channel = Mock(autospec=True, create=True)
    with caplog.at_level(logging.INFO):
        queue.queue_setup(channel=channel)

    channel.exchange_declare.assert_called_with(
        exchange="health-assess-exchange",
        exchange_type="direct",
        durable=True,
        auto_delete=True,
    )
    channel.queue_declare.assert_called_with(queue=f"health-assess.{service_queue_name}", durable=True, auto_delete=True)
    channel.queue_bind.assert_called_with(
        queue=f"health-assess.{service_queue_name}", exchange="health-assess-exchange"
    )
    assert channel.basic_consume

    assert (
        f" [*] Waiting for data for queue: health-assess.{service_queue_name}. To exit press CTRL+C"
        in caplog.text
    )


@pytest.mark.parametrize(
    "queue, diagnosticCode, body, main",
    [
        (q6602, "6602", {"evidence": "some medical data body"}, main6602),
    ],
)
def test_on_request_callback(queue, diagnosticCode, body, main, caplog):

    channel = Mock(autospec=True, create=True)
    method = Mock(autospec=True, create=True)
    properties = Mock(autospec=True, create=True)
    properties.correlation_id = 1234
    properties.reply_to = "some_queue"
    method.routing_key = diagnosticCode

    body_formatted = json.dumps(body).encode("utf-8")

    with caplog.at_level(logging.INFO):
        with patch(
            f"assessclaimdc{diagnosticCode}.src.lib.main.{main.__name__}",
            return_value=True,
        ):
            queue.on_request_callback(channel, method, properties, body_formatted)

    assert (
        f" [x] {diagnosticCode}: Received message."
        in caplog.text
    )
    assert (
        f" [x] {diagnosticCode}: Message sent." in caplog.text
    )
