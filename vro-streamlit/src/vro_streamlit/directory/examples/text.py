# mypy: ignore-errors

import numpy as np
import pandas as pd
import streamlit as st

st.header('Streamlit text elements')
st.title('Title')
st.header('Header')
st.subheader('subheader')
st.markdown('~~lorem~~ *ipsum* **dolor** `sit` ***amet***')
st.code('code')
st.caption('caption')
st.divider()

st.header('2 Columns')
left_column, right_column = st.columns(2)
# You can use a column just like st.sidebar:
left_column.button('Press me!')

# Or even better, call Streamlit functions inside a "with" block:
with right_column:
    chosen = st.radio('Sorting hat', ('Gryffindor', 'Ravenclaw', 'Hufflepuff', 'Slytherin'))
    st.write(f'You are in {chosen} house!')

st.header('3 Columns')
left, middle, right = st.columns(3)
left.write('left')
middle.write('middle')
right.write('right')


'Checkbox'
if st.checkbox('Show dataframe', value=True):
    chart_data = pd.DataFrame(np.random.randn(20, 3), columns=['a', 'b', 'c'])
    st.dataframe(chart_data, use_container_width=True)

'Slider'
x = st.slider('x', min_value=-100, max_value=1000)  # 👈 this is a widget
st.write(x, 'squared is', x * x)
