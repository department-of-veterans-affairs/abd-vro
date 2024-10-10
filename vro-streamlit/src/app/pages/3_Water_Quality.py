import pandas as pd
import streamlit as st

# sidebar config
st.sidebar.image('static/streamlit-logo.png')
st.sidebar.button('Rerun')

if 'color' not in st.session_state:
    st.session_state.color = '#FF0000'

st.sidebar.write('Choose a datapoint color')
st.session_state.color = st.sidebar.color_picker('Color', st.session_state.color)

st.title('Water Quality (pH)')
st.markdown('From [fws.gov](https://ecos.fws.gov/ServCat/DownloadFile/173741?Reference=117348)')
df = pd.DataFrame(pd.read_csv('https://ecos.fws.gov/ServCat/DownloadFile/173741?Reference=117348'), columns=['Read_Date', 'pH (standard units)'])

st.dataframe(df, use_container_width=True)

st.line_chart(df, x='Read_Date', color=st.session_state.color)
