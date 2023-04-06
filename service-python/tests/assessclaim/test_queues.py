import json
import logging
from unittest.mock import Mock, patch

import pytest

from pdfgenerator.src.lib import queues as pdf_queues
from assessclaimcancer.src.lib import queues as q_cancer
from assessclaimrespiratory.src.lib import queues as q_respiratory
from assessclaimdc6510.src.lib import queues as q6510
from assessclaimdc6510.src.lib.main import assess_sinusitis as main6510
from assessclaimdc6522.src.lib import queues as q6522
from assessclaimdc6522.src.lib.main import assess_rhinitis as main6522
from assessclaimdc6602.src.lib import queues as q6602
from assessclaimdc6602.src.lib.main import assess_asthma as main6602
from assessclaimdc6602v2.src.lib import queues as q6602v2
from assessclaimdc6602v2.src.lib.main import assess_asthma as main6602v2
from assessclaimdc7101.src.lib import queues as q7101


@pytest.mark.parametrize(
    "queue, service_queue_name, exchange", [
        (q6602v2, "health-assess.6602v2", "health-assess-exchange"),
        (q6602, "health-assess.6602", "health-assess-exchange"),
        (q7101, "health-assess.7101", "health-assess-exchange"),
        (q6522, "health-assess.6522", "health-assess-exchange"),
        (q_cancer, "health-assess.cancer", "health-assess-exchange"),
        (q_respiratory, "health-assess.respiratory", "health-assess-exchange"),
    ]
)
def test_queue_setup(queue, service_queue_name, exchange, caplog):
    queue_name = service_queue_name
    channel = Mock(autospec=True, create=True)
    with caplog.at_level(logging.INFO):
        queue.queue_setup(channel=channel)

    channel.exchange_declare.assert_called_with(
        exchange=exchange,
        exchange_type="direct",
        durable=True,
        auto_delete=True,
    )

    channel.queue_declare.assert_called_with(queue=queue_name, durable=True, auto_delete=True)
    channel.queue_bind.assert_called_with(
        queue=queue_name, exchange=exchange
    )
    assert channel.basic_consume

    assert (
        f" [*] Waiting for data for queue: {queue_name}. To exit press CTRL+C"
        in caplog.text
    )


@pytest.mark.parametrize(
    "queue, diagnosticCode, body, main",
    [
        (q6602, "6602", {"evidence": "some medical data body",
                         "claimSubmissionId": "1234"}, main6602),
        (q6602v2, "6602v2", {"evidence": "some medical data body",
                             "claimSubmissionId": "1234"}, main6602v2),
        (q6510, "6510", {"evidence": "some medical data body",
                         "claimSubmissionId": "1234"}, main6510),
        (q6522, "6522", {"evidence": "some medical data body",
                         "claimSubmissionId": "1234"}, main6522)
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
            return_value={"claimSubmissionId": "1234"},
        ):
            queue.on_request_callback(channel, method, properties, body_formatted)

    assert (
        f"claimSubmissionId: 1234, health data received by {diagnosticCode}"
        in caplog.text
    )
    assert (
        f"claimSubmissionId: 1234, evaluation sent by {diagnosticCode}" in caplog.text
    )
