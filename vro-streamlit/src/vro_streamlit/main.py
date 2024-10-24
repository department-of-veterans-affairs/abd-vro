import streamlit as st

import vro_streamlit.auth.auth_service as auth
import vro_streamlit.config as config
import vro_streamlit.directory.home as home
from vro_streamlit.directory.bie_events import claim_events, contention_events

LOGIN_BUTTON = 'sidebar_login_button'
LOGOUT_BUTTON = 'sidebar_logout_button'

st.set_page_config(page_title='VRO Streamlit', layout='wide')


def init_session_state() -> None:
    st.session_state.setdefault('database_connected', True)
    st.session_state.setdefault('user', None)


def update_login_status() -> None:
    if not st.session_state.user:
        st.session_state.user = auth.log_in()
    else:
        if auth.log_out():
            st.session_state.user = None


def create_navigation() -> None:
    home_page = st.Page(home.show, title='Home', default=True)
    # BIE events
    bie_events = [
        st.Page(claim_events.show, title='Claim Events', url_path='/claim_events'),
        st.Page(contention_events.show, title='Contention Events', url_path='/contention_events'),
    ]
    # examples
    examples = [
        st.Page('directory/examples/text.py', title='Text'),
        st.Page('directory/examples/dataframes.py', title='Dataframes'),
        st.Page('directory/examples/water_quality.py', title='Water Quality'),
    ]
    nav = st.navigation({'Home': [home_page], 'BIE Events': bie_events, 'Examples': examples})
    nav.run()


def create_sidebar() -> None:
    with st.sidebar:
        with st.container(border=True):
            col1, col2 = st.columns(2)
            with col1:
                st.markdown('Environment', help='Current operating environment')
                st.markdown('Database', help='Database connection status')
                st.markdown('Authorized', help='User authorization status')
            with col2:
                st.markdown(f'`{config.ENV}`')
                st.markdown(':large_green_circle:' if st.session_state.database_connected else ':red_circle:', unsafe_allow_html=True)
                st.markdown(':large_green_circle:' if st.session_state.user else ':red_circle:', unsafe_allow_html=True)

        if st.session_state.user:
            st.button('Log Out', use_container_width=True, on_click=update_login_status, key=LOGOUT_BUTTON)
        else:
            st.button('Log In', use_container_width=True, on_click=update_login_status, key=LOGIN_BUTTON)


if __name__ == '__main__':
    init_session_state()
    create_sidebar()
    create_navigation()
