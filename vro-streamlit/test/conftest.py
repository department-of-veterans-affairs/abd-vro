from unittest.mock import Mock

import pytest

from vro_streamlit.auth.user import User

"""Pytest configuration. This file is automatically loaded by pytest before any test."""
APP_TEST_TIMEOUT = 5

USERNAME = 'test'


@pytest.fixture(autouse=True)
def auth_service(mocker):
    auth_service = Mock()
    auth_service.log_in.return_value = User(USERNAME)
    auth_service.log_out.return_value = True
    return mocker.patch('vro_streamlit.auth.auth_service', auth_service)
