# mypy: ignore-errors
# Ignore until this class is implemented

import streamlit as st

from vro_streamlit.service.database import EVENTS_REPO


def show():  # pragma: no cover
    st.header('BIE Contention Events')
    st.markdown(
        'The table below contains BIE Contention Events. For more information and descriptions of the individual fields. Please refer to the '
        '[BIE Contention Events](https://github.com/department-of-veterans-affairs/abd-vro/wiki/BIE-Contention-Events-User-Guide) page in the VRO Wiki.'
    )
    st.markdown('*The data in this table is for demonstration purposes only and does not reflect real data.*')

    st.dataframe(
        EVENTS_REPO.get_contention_events(),
        hide_index=True,
    )


if __name__ == '__main__':
    show()
