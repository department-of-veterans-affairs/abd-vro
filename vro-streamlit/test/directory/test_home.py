import pytest
from streamlit.testing.v1 import AppTest

from vro_streamlit.auth.user import User
from vro_streamlit.directory.home import LOGIN_BUTTON

from ..conftest import APP_TEST_TIMEOUT, USERNAME
from ..util import assert_button_contains_label, assert_markdown_contains_values


@pytest.fixture()
def app_test():
    app_test = AppTest.from_file('src/vro_streamlit/directory/home.py', default_timeout=APP_TEST_TIMEOUT)
    app_test.session_state.user = None
    return app_test


def test_home(app_test) -> None:
    app_test.run()
    assert not app_test.exception
    assert app_test.header[0].value == 'Home'
    assert app_test.subheader[0].value == 'Welcome to the home page!'


def test_home_user_is_none(app_test) -> None:
    # Initial page load
    app_test.run()
    assert not app_test.exception
    assert_markdown_contains_values(app_test.markdown, 'Please Login')
    assert_button_contains_label(app_test.button[0], 'Log In')

    # Click page which reloads page
    app_test.button(key=LOGIN_BUTTON).click().run()
    assert_markdown_contains_values(app_test.markdown, f'Hello, {USERNAME}!')
    assert_button_contains_label(app_test.button[0], 'Log Out')


def test_home_user_is_not_none(app_test) -> None:
    # Initial page load
    app_test.session_state.user = User(USERNAME)
    app_test.run()
    assert not app_test.exception
    assert_markdown_contains_values(app_test.markdown, f'Hello, {USERNAME}!')
    assert_button_contains_label(app_test.button[0], 'Log Out')

    # Click page which reloads page
    app_test.button(key=LOGIN_BUTTON).click().run()
    assert_markdown_contains_values(app_test.markdown, 'Please Login')
    assert_button_contains_label(app_test.button[0], 'Log In')
