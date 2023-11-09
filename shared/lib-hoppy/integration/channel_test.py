import pytest
from unittest.mock import MagicMock
from hoppy.channel import Channel
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


@pytest.fixture
def channel():
    return Channel()


@pytest.mark.asyncio
async def test_connect(channel):
    await channel.connect()
    assert channel.is_ready


def test_ready(channel):
    channel._ready()
    assert channel._is_ready


def test_shut_down(channel, mocker):
    mock_close_channel = mocker.patch.object(channel, '_close_channel')
    mock_close_connection = mocker.patch.object(channel, '_close_connection')
    channel._shut_down()
    mock_close_channel.assert_called_once()
    mock_close_connection.assert_called_once()


def test_on_channel_open(channel, mocker):
    mock_channel = MagicMock()
    mock_ready = mocker.patch.object(channel, '_ready')
    channel._on_channel_open(mock_channel)
    mock_ready.assert_called_once()


def test_exchange_declare(channel, mocker):
    mock_exchange_properties = MagicMock(spec=ExchangeProperties)
    mock_set_exchange_properties = mocker.patch.object(channel, '_set_exchange_properties')
    mock_setup_exchange = mocker.patch.object(channel, '_setup_exchange')
    channel.exchange_declare(mock_exchange_properties)
    mock_set_exchange_properties.assert_called_once_with(mock_exchange_properties)
    mock_setup_exchange.assert_called_once()


def test_queue_declare(channel, mocker):
    mock_queue_properties = MagicMock(spec=QueueProperties)
    mock_set_queue_properties = mocker.patch.object(channel, '_set_queue_properties')
    mock_setup_queue = mocker.patch.object(channel, '_setup_queue')
    channel.queue_declare(mock_queue_properties, 'exchange_name', 'routing_key')
    mock_set_queue_properties.assert_called_once_with(mock_queue_properties)
    mock_setup_queue.assert_called_once()
