# mypy: ignore-errors

import pandas as pd
import streamlit as st

# sidebar config
st.title('Water Quality (pH)')
st.markdown('From [fws.gov](https://ecos.fws.gov/ServCat/DownloadFile/173741?Reference=117348)')
df = pd.DataFrame(pd.read_csv('https://ecos.fws.gov/ServCat/DownloadFile/173741?Reference=117348'), columns=['Read_Date', 'pH (standard units)'])

st.dataframe(df, use_container_width=True)

st.line_chart(df, x='Read_Date')
