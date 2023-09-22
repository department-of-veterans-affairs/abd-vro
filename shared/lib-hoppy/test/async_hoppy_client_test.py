import asyncio
import json
import uuid
from unittest.mock import ANY, call

import pika
import pika.spec
import pytest
from hoppy.async_hoppy_client import (AsyncHoppyClient,
                                      RetryableAsyncHoppyClient)
from hoppy.exception import ResponseException

config = {}


@pytest.fixture(autouse=True)
def mock_async_consumer(mocker):
    return mocker.patch('hoppy.async_consumer.AsyncConsumer')


@pytest.fixture(autouse=True)
def mock_async_publisher(mocker):
    return mocker.patch('hoppy.async_publisher.AsyncPublisher')


def get_client(mock_async_publisher, mock_async_consumer, app_id="test", exchange="exchange", queue="queue",
               reply_queue="reply_queue", max_latency=3, requeue_attempts=3):
    client = AsyncHoppyClient("test_client", app_id, config, exchange, queue, reply_queue, max_latency, requeue_attempts)
    client.async_publisher = mock_async_publisher
    client.async_consumer = mock_async_consumer
    return client


class TestAsyncHoppyClient:
    def test_start(self, mock_async_publisher, mock_async_consumer):
        # given
        client = get_client(mock_async_publisher, mock_async_consumer)

        # when
        loop = asyncio.new_event_loop()
        client.start(loop)

        client.async_publisher.connect.assert_called_once_with(loop)
        client.async_consumer.connect.assert_called_once_with(loop)

    def test_stop(self, mock_async_publisher, mock_async_consumer):
        # Given
        client = get_client(mock_async_publisher, mock_async_consumer)

        # When
        client.stop()

        # Then
        client.async_publisher.stop.assert_called_once()
        client.async_consumer.stop.assert_called_once()

    @pytest.mark.asyncio
    async def test_make_request_and_timeout_reached(self, mock_async_publisher, mock_async_consumer):
        # Given
        client = get_client(mock_async_publisher, mock_async_consumer, max_latency=0)
        request = '{"test":1}'

        # When / Then
        with pytest.raises(ResponseException):
            await client.make_request("1", body=request)
        client.async_publisher.publish_message.assert_called_once_with(request, ANY)

    @pytest.mark.asyncio
    async def test_make_request_and_correlated_response_received(self, mock_async_publisher, mock_async_consumer,
                                                                 mocker):
        # Given
        client = get_client(mock_async_publisher, mock_async_consumer, max_latency=3)
        request = '{"test":1}'
        expected_response = '{"test_response":1}'
        cor_id = str(uuid.uuid4())
        mocker.patch('uuid.uuid4', return_value=cor_id)
        reply_props = pika.BasicProperties(correlation_id=cor_id)
        deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        # When
        results_future = asyncio.ensure_future(client.make_request('1', request))
        await asyncio.sleep(0)
        client._on_reply(None, reply_props, deliver_props, expected_response)

        # Then
        actual_response = await asyncio.wait_for(results_future, 10)
        client.async_publisher.publish_message.assert_called_once_with(request, ANY)
        assert json.loads(expected_response) == actual_response  # actual response is json object

    @pytest.mark.asyncio
    async def test_make_request_and_non_correlated_response_received(self, mock_async_publisher, mock_async_consumer,
                                                                     mocker):
        # Given
        client = get_client(mock_async_publisher, mock_async_consumer, max_latency=2)

        # Request
        request = '{"test":1}'
        request_correlation_id = str(uuid.uuid4())

        # Reply
        unexpected_response = '{"test_response":1}'
        reply_correlation_id = str(uuid.uuid4())
        reply_props = pika.BasicProperties(correlation_id=reply_correlation_id)
        deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        # When
        mocker.patch('uuid.uuid4', return_value=request_correlation_id)
        results_future = asyncio.ensure_future(client.make_request('1', request))
        await asyncio.sleep(0)
        client._on_reply(None, reply_props, deliver_props, unexpected_response)

        # Then should time out
        with pytest.raises(ResponseException):
            await asyncio.wait_for(results_future, 3)
        client.async_publisher.publish_message.assert_called_once_with(request, ANY)
        assert request_correlation_id not in client.responses.keys()

        # reply_correlation_id is still in rejected. Limit of response_reject_and_requeue_attempts has not been reached.
        assert reply_correlation_id in client.rejected.keys()

    @pytest.mark.asyncio
    async def test_make_request_and_non_correlated_response_received_multiple_times(self,
                                                                                    mock_async_publisher,
                                                                                    mock_async_consumer,
                                                                                    mocker):
        # Given
        client = get_client(mock_async_publisher, mock_async_consumer, max_latency=3)

        # Request
        request = '{"test":1}'
        request_correlation_id = str(uuid.uuid4())

        # Reply
        unexpected_response = '{"test_response":1}'
        reply_correlation_id = str(uuid.uuid4())
        reply_props = pika.BasicProperties(correlation_id=reply_correlation_id)
        deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        # When
        mocker.patch('uuid.uuid4', return_value=request_correlation_id)
        results_future = asyncio.ensure_future(client.make_request('1', request))
        await asyncio.sleep(0)
        client._on_reply(None, reply_props, deliver_props, unexpected_response)
        client._on_reply(None, reply_props, deliver_props, unexpected_response)
        client._on_reply(None, reply_props, deliver_props, unexpected_response)

        # Then should time out
        with pytest.raises(ResponseException):
            await asyncio.wait_for(results_future, 5)
        client.async_publisher.publish_message.assert_called_once_with(request, ANY)

        # reply_correlation_id is not in rejected. Limit of response_reject_and_requeue_attempts has been reached.
        assert reply_correlation_id not in client.rejected.keys()
        assert request_correlation_id not in client.responses.keys()

    @pytest.mark.asyncio
    async def test_make_request_and_correlated_and_non_correlated_response_received(self,
                                                                                    mock_async_publisher,
                                                                                    mock_async_consumer,
                                                                                    mocker):
        # Given
        client = get_client(mock_async_publisher, mock_async_consumer, max_latency=3)

        # Request
        request = '{"test":1}'
        request_correlation_id = str(uuid.uuid4())

        # Reply
        expected_response = '{"test_response":1}'
        expected_response_correlation_id = request_correlation_id
        expected_response_props = pika.BasicProperties(correlation_id=expected_response_correlation_id)
        expected_deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        unexpected_response = '{"test_response":2}'
        unexpected_response_correlation_id = str(uuid.uuid4())
        unexpected_response_props = pika.BasicProperties(correlation_id=unexpected_response_correlation_id)
        unexpected_deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        # When
        mocker.patch('uuid.uuid4', return_value=request_correlation_id)
        results_future = asyncio.ensure_future(client.make_request('1', request))
        await asyncio.sleep(0)
        client._on_reply(None, unexpected_response_props, unexpected_deliver_props, unexpected_response)
        client._on_reply(None, expected_response_props, expected_deliver_props, expected_response)

        # Then
        client.async_publisher.publish_message.assert_called_once_with(request, ANY)
        actual_response = await asyncio.wait_for(results_future, 10)
        assert json.loads(expected_response) == actual_response  # actual response is json object
        assert request_correlation_id not in client.responses.keys()

        # reply_correlation_id is still in rejected. Limit of response_reject_and_requeue_attempts has not been reached.
        assert unexpected_response_correlation_id in client.rejected.keys()


def get_retry_client(mock_async_publisher, mock_async_consumer, app_id="test", exchange="exchange", queue="queue",
                     reply_queue="reply_queue", max_latency=3, requeue_attempts=3, max_retries=3):
    client = RetryableAsyncHoppyClient("test_client", app_id, config, exchange, queue, reply_queue, max_latency, requeue_attempts,
                                       max_retries)
    client.async_publisher = mock_async_publisher
    client.async_consumer = mock_async_consumer
    return client


class TestRetryableAsyncHoppyClient:

    @pytest.mark.asyncio
    async def test_make_request_and_max_retries_reached(self, mock_async_publisher, mock_async_consumer, mocker):
        # Given
        client = get_retry_client(mock_async_publisher, mock_async_consumer, max_latency=1, max_retries=2)
        request = '{"test":1}'

        correlation_id_1 = uuid.uuid4()
        correlation_id_2 = uuid.uuid4()
        mocker.patch('uuid.uuid4', side_effect=[correlation_id_1, correlation_id_2])
        properties_1 = pika.spec.BasicProperties(app_id="test", content_type="application/json", reply_to="reply_queue",
                                                 correlation_id=str(correlation_id_1))
        properties_2 = pika.spec.BasicProperties(app_id="test", content_type="application/json", reply_to="reply_queue",
                                                 correlation_id=str(correlation_id_2))
        mocker.patch('pika.spec.BasicProperties', side_effect=[properties_1, properties_2])

        # When / Then
        with pytest.raises(ResponseException):
            await client.make_request("1", body=request)
        expected_calls = [call(request, properties_1), call(request, properties_2)]
        assert str(mock_async_publisher.publish_message.mock_calls).replace('\n', '') == str(expected_calls)

    @pytest.mark.asyncio
    async def test_make_request_and_initial_attempt_success(self,
                                                            mock_async_publisher,
                                                            mock_async_consumer,
                                                            mocker):
        # Given
        client = get_retry_client(mock_async_publisher, mock_async_consumer, max_latency=3, max_retries=2)
        request = '{"test":1}'
        expected_response = '{"test_response":1}'
        cor_id = str(uuid.uuid4())
        mocker.patch('uuid.uuid4', return_value=cor_id)
        reply_props = pika.BasicProperties(correlation_id=cor_id)
        deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        # When
        results_future = asyncio.ensure_future(client.make_request('1', request))
        await asyncio.sleep(0)
        client._on_reply(None, reply_props, deliver_props, expected_response)

        # Then
        actual_response = await asyncio.wait_for(results_future, 10)
        client.async_publisher.publish_message.assert_called_once_with(request, ANY)
        assert json.loads(expected_response) == actual_response  # actual response is json object

    @pytest.mark.asyncio
    async def test_make_request_and_initial_attempt_fails_retry_succeeds(self,
                                                                         mock_async_publisher,
                                                                         mock_async_consumer,
                                                                         mocker):
        # Given
        client = get_retry_client(mock_async_publisher, mock_async_consumer, max_latency=5, max_retries=2)

        # Request
        request = '{"test":1}'
        correlation_id_1 = uuid.uuid4()
        correlation_id_2 = uuid.uuid4()
        mocker.patch('uuid.uuid4', side_effect=[correlation_id_1, correlation_id_2])
        properties_1 = pika.spec.BasicProperties(app_id="test", content_type="application/json", reply_to="reply_queue",
                                                 correlation_id=str(correlation_id_1))
        properties_2 = pika.spec.BasicProperties(app_id="test", content_type="application/json", reply_to="reply_queue",
                                                 correlation_id=str(correlation_id_2))
        mocker.patch('pika.spec.BasicProperties', side_effect=[properties_1, properties_2])

        # Response
        expected_response = '{"test_response":1}'
        reply_props = pika.BasicProperties(correlation_id=str(correlation_id_2))
        deliver_props = pika.spec.Basic.Deliver(delivery_tag=1)

        # When
        mocker.patch('hoppy.async_publisher.AsyncPublisher.publish_message', side_effect=[Exception("Ooops"), None])
        results_future = asyncio.ensure_future(client.make_request('1', request))
        await asyncio.sleep(0)
        client._on_reply(None, reply_props, deliver_props, expected_response)

        # Then
        actual_response = await asyncio.wait_for(results_future, 10)
        expected_calls = [call(request, properties_1), call(request, properties_2)]
        assert str(mock_async_publisher.publish_message.mock_calls).replace('\n', '') == str(expected_calls)
        assert json.loads(expected_response) == actual_response  # actual response is json object
