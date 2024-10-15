import time

import numpy as np
import pandas as pd
import streamlit as st
from numpy._typing import NDArray

# sidebar config
st.sidebar.image('static/streamlit-logo.png')
st.sidebar.button('Rerun')

if 'color' not in st.session_state:
    st.session_state.color = '#FF0000'

st.sidebar.write('Choose a datapoint color')
st.session_state.color = st.sidebar.color_picker('Color', st.session_state.color)

'Random line chart'
last_rows = np.random.randn(1, 1)
chart = st.line_chart(last_rows, color=st.session_state.color)
progress = st.progress(0)

for i in range(1, 101):
    new_rows = last_rows[-1, :] + np.random.randn(5, 1).cumsum(axis=0)
    chart.add_rows(new_rows)
    last_rows = new_rows
    time.sleep(0.01)
    progress.progress(i)

'Basic table'
df = pd.DataFrame({'first column': [1, 2, 3, 4], 'second column': [10, 20, 30, 40]})
st.write(df)


@st.cache_data
def rand_5_8() -> NDArray[np.float64]:
    return np.random.randn(5, 8)


'Dataframe example'
npDataframe = rand_5_8()
st.dataframe(npDataframe)

'Stylized dataframe with cache'
dataframe = pd.DataFrame(rand_5_8(), columns=('col %d' % i for i in range(8)))

st.dataframe(dataframe.style.highlight_max(axis=0))

'Line chart'
chart_data = pd.DataFrame(np.random.randn(8, 3), columns=['a', 'b', 'c'])

st.line_chart(chart_data)

'Map'
map_data = pd.DataFrame(np.random.randn(100, 2) / [50, 50] + [38.66, -78.48], columns=['lat', 'lon'])

st.map(map_data, color=st.session_state.color)

'Selection boxes'
option: int = st.selectbox('Which number do you like best?', df['first column'])

'You selected: ', option

'Scatterplot'
if 'df' not in st.session_state:
    st.session_state.df = pd.DataFrame(np.random.randn(20, 2), columns=['x', 'y'])

st.divider()
st.scatter_chart(st.session_state.df, x='x', y='y', color=st.session_state.color)
