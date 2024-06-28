from datetime import datetime

import pytest
from schema.contention import ContentionSummary
from schema.get_contentions import Response
from util.contentions_util import ContentionsUtil

new_tendinitis = ContentionSummary(
    medical_ind=True,
    begin_date=datetime(2023, 1, 1),
    last_modified=datetime(2023, 1, 1),
    contention_type_code='NEW',
    claimant_text='tendinitis/bilateral',
    contention_id=1,
)
new_tinnitus = ContentionSummary(
    medical_ind=True, begin_date=datetime(2023, 1, 1), last_modified=datetime(2023, 1, 1), contention_type_code='NEW', claimant_text='tinnitus', contention_id=1
)
increase_tendinitis = ContentionSummary(
    medical_ind=True,
    begin_date=datetime(2023, 1, 1),
    last_modified=datetime(2023, 1, 1),
    contention_type_code='INC',
    claimant_text='tendinitis/bilateral',
    contention_id=1,
)


@pytest.fixture
def pending_contentions():
    return Response(contentions=[new_tendinitis, new_tinnitus], status_code=200, status_message='OK')


@pytest.fixture
def ep400_contentions():
    return Response(contentions=[new_tinnitus, increase_tendinitis], status_code=200, status_message='OK')


def test_new_contentions_successful(pending_contentions, ep400_contentions):
    new_contentions = ContentionsUtil.new_contentions(pending_contentions.contentions, ep400_contentions.contentions)
    expected_contentions = [increase_tendinitis]
    assert new_contentions == expected_contentions


def test_new_contentions_missing_data(pending_contentions, ep400_contentions):
    assert len(ContentionsUtil.new_contentions(pending_contentions.contentions, None)) == 0

    assert len(ContentionsUtil.new_contentions(None, ep400_contentions.contentions)) == len(ep400_contentions.contentions)
    assert len(ContentionsUtil.new_contentions(None, None)) == 0
    assert len(ContentionsUtil.new_contentions([], None)) == 0
    assert len(ContentionsUtil.new_contentions(None, [])) == 0
    assert len(ContentionsUtil.new_contentions([], [])) == 0
