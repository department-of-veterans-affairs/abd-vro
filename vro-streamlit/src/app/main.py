import streamlit as st

import app.config as config


def main() -> None:
    # sidebar config
    st.sidebar.image('static/streamlit-logo.png')
    st.sidebar.button('Rerun')

    if 'color' not in st.session_state:
        st.session_state.color = '#FF0000'

    st.sidebar.write('Choose a datapoint color')
    st.session_state.color = st.sidebar.color_picker('Color', st.session_state.color)

    st.header('Hello world!')
    st.subheader(f'Environment: {config.ENV}')


if __name__ == '__main__':
    main()
