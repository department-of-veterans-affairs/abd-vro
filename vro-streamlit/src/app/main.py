import streamlit as st


def main() -> None:
    # sidebar config
    st.sidebar.image('static/streamlit-logo.png')
    st.sidebar.button('Rerun')

    if 'color' not in st.session_state:
        st.session_state.color = '#FF0000'

    st.sidebar.write('Choose a datapoint color')
    st.session_state.color = st.sidebar.color_picker('Color', st.session_state.color)

    st.header('Hello world!')
