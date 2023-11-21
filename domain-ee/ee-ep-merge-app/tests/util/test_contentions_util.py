from datetime import datetime
import pytest
from model.get_contentions import Response
from model.contention import ContentionSummary
from util.contentions_util import CompareException, ContentionsUtil, MergeException

new_tendinitis = ContentionSummary(
    medical_ind=True,
    begin_date=datetime(2023, 1, 1),
    last_modified=datetime(2023, 1, 1),
    contention_type_code='NEW',
    claimant_text='tendinitis/bilateral',
    contention_id=1
)
new_tinnitus = ContentionSummary(
    medical_ind=True,
    begin_date=datetime(2023, 1, 1),
    last_modified=datetime(2023, 1, 1),
    contention_type_code='NEW',
    claimant_text='tinnitus',
    contention_id=1
)
increase_tendinitis = ContentionSummary(
    medical_ind=True,
    begin_date=datetime(2023, 1, 1),
    last_modified=datetime(2023, 1, 1),
    contention_type_code='INCREASE',
    claimant_text='tendinitis/bilateral',
    contention_id=1
)


@pytest.fixture
def pending_contentions():
    return Response(contentions=[new_tendinitis, new_tinnitus], status_code=200, status_message='OK')


@pytest.fixture
def ep400_contentions():
    return Response(contentions=[new_tinnitus, increase_tendinitis], status_code=200, status_message='OK')


def test_merge_claims_successful(pending_contentions, ep400_contentions):
    merged_contentions = ContentionsUtil.merge_claims(pending_contentions, ep400_contentions)
    expected_contentions = [new_tendinitis, new_tinnitus, increase_tendinitis]
    assert merged_contentions == expected_contentions


def test_merge_claims_missing_data(pending_contentions, ep400_contentions):
    with pytest.raises(MergeException):
        ContentionsUtil.merge_claims(pending_contentions, None)

    with pytest.raises(MergeException):
        ContentionsUtil.merge_claims(None, ep400_contentions)


def test_new_contentions_successful(pending_contentions, ep400_contentions):
    new_contentions = ContentionsUtil.new_contentions(pending_contentions.contentions, ep400_contentions.contentions)
    expected_contentions = [increase_tendinitis]
    assert new_contentions == expected_contentions


def test_new_contentions_missing_data(pending_contentions, ep400_contentions):
    with pytest.raises(CompareException):
        ContentionsUtil.new_contentions(pending_contentions.contentions, None)

    with pytest.raises(CompareException):
        ContentionsUtil.new_contentions(None, ep400_contentions.contentions)
