from functools import partial
from importlib.resources import files

import streamlit as st

import vro_streamlit.auth.auth_service as auth

LOGIN_BUTTON = 'home_login_button'
LOGO = files('vro_streamlit').joinpath('static/streamlit-logo.png').read_bytes()


def update_login_status(user_logged_in: bool) -> None:
    if not user_logged_in:
        st.session_state.user = auth.log_in()
    else:
        if auth.log_out():
            st.session_state.user = None


def show() -> None:
    col1, col2 = st.columns([0.04, 0.96])
    col1.image(LOGO, width=100)
    col2.header('Home')
    st.subheader('Welcome to the home page!')

    user_logged_in = 'user' in st.session_state and st.session_state.user is not None

    msg = 'Please Login' if not user_logged_in else f'Hello, {st.session_state.user.username}!'
    st.write(msg)

    button_text = 'Log Out' if user_logged_in else 'Log In'

    st.button(button_text, key=LOGIN_BUTTON, on_click=partial(update_login_status, user_logged_in))


if __name__ == '__main__':
    show()
