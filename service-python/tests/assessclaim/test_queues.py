import pytest
import logging
import json
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
def test_queue_setup(queue, service_queue_name, caplog):
    channel = Mock(autospec=True, create=True)
    with caplog.at_level(logging.INFO):
        queue.queue_setup(channel=channel)

    channel.exchange_declare.assert_called_with(
        exchange="health-assess-exchange", exchange_type="direct", durable=True, auto_delete=True)
    channel.queue_declare.assert_called_with(queue=service_queue_name)
    channel.queue_bind.assert_called_with(queue=service_queue_name, exchange="health-assess-exchange")
    assert channel.basic_consume

    assert f' [*] Waiting for data for queue: {service_queue_name}. To exit press CTRL+C' in caplog.text


@pytest.mark.parametrize(
    "queue, diagnosticCode, body",
    [
        (
                q6602,
                "6602",
                {"evidence": "some medical data body"}
        ),
        (
                q7101,
                "7101",
                {"evidence": "some medical data body"}
        )
    ]
)
def test_on_request_callback(queue, diagnosticCode, body, caplog):

    channel = Mock(autospec=True, create=True)
    method = Mock(autospec=True, create=True)
    properties = Mock(autospec=True, create=True)
    properties.correlation_id = 1234
    properties.reply_to = "some_queue"
    method.routing_key = diagnosticCode
    main = Mock()
    main.assess_hypertension = Mock(return_value=True)
    body_formatted = json.dumps(body).encode('utf-8')

    with caplog.at_level(logging.INFO):

        queue.on_request_callback(channel, method, properties, body_formatted)

    assert f" [x] {diagnosticCode}: Received message: {properties.correlation_id}" in caplog.text
    assert f" [x] {diagnosticCode}: Message sent to: {properties.reply_to}"
