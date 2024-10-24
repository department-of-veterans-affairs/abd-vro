# mypy: ignore-errors
# Ignore until this class is implemented

import streamlit as st

from vro_streamlit.service.database import EVENTS_REPO


def show():  # pragma: no cover
    st.header('BIE Claim Events')
    st.markdown(
        'The table below contains BIE Claim Events. For more information and descriptions of the individual fields. Please refer to the '
        '[BIE Claim Events](https://confluence.devops.va.gov/display/VAExternal/Subdomain%3A+Claim+Events) domain page for BIE Events.'
    )
    st.markdown('*The data in this table is for demonstration purposes only and does not reflect real data.*')

    st.dataframe(
        EVENTS_REPO.get_claim_events(),
        hide_index=True,
    )


if __name__ == '__main__':
    show()
