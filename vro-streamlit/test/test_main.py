from test.conftest import APP_TEST_TIMEOUT, USERNAME

import pytest
from streamlit.testing.v1 import AppTest
from util import assert_markdown_contains_all_values

from vro_streamlit.auth.user import User


@pytest.fixture()
def app_test():
    app_test = AppTest.from_file('src/vro_streamlit/main.py', default_timeout=APP_TEST_TIMEOUT)
    app_test.session_state.user = None
    app_test.session_state.database_connected = False
    return app_test


@pytest.mark.parametrize(
    'db_connected, db_connected_icon, user, authorized_icon',
    [
        pytest.param(False, ':red_circle:', None, ':red_circle:', id='not connected, not authorized'),
        pytest.param(True, ':large_green_circle:', None, ':red_circle:', id='connected, not authorized'),
        pytest.param(False, ':red_circle:', User(USERNAME), ':large_green_circle:', id='not connected, authorized'),
        pytest.param(True, ':large_green_circle:', User(USERNAME), ':large_green_circle:', id='connected, authorized'),
    ],
)
def test_main_not_logged_in(app_test, db_connected, db_connected_icon, user, authorized_icon) -> None:
    app_test.session_state.user = user
    app_test.session_state.database_connected = db_connected
    app_test.run()
    assert not app_test.exception

    # sidebar populated
    assert_markdown_contains_all_values(
        app_test.sidebar.markdown,
        [
            'Environment',
            'Database',
            'Authorized',
            '`test-environment`',
            db_connected_icon,
            authorized_icon,
        ],
    )


def test_main_defaults(app_test) -> None:
    app_test.run()
    assert not app_test.exception

    # session state defaults
    assert 'database_connected' in app_test.session_state
    assert 'user' in app_test.session_state

    # sidebar populated
    assert_markdown_contains_all_values(
        app_test.sidebar.markdown,
        ['Environment', 'Database', 'Authorized', '`test-environment`', ':red_circle:', ':red_circle:'],
    )
