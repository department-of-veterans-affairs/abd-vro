"""Pytest configuration. This file is automatically loaded by pytest before any tests."""

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import StaticPool, create_engine
from sqlmodel import Session, SQLModel

from app.api import app
from app.database.config import get_session


# create fixture for tracked_claim_repo to add side effects to calls
@pytest.fixture
def mock_repo(mocker):
    repo = mocker.MagicMock()
    repo.add = mocker.MagicMock()
    repo.is_ready = mocker.MagicMock(return_value=True)
    return mocker.patch('app.api.tracked_claim_repo', return_value=repo)


@pytest.fixture
def session_override():
    engine = create_engine('sqlite://', connect_args={'check_same_thread': False}, poolclass=StaticPool)
    SQLModel.metadata.create_all(engine)
    with Session(engine) as session:
        yield session


@pytest.fixture
def client(session_override) -> TestClient:
    def get_session_override():
        return session_override

    app.dependency_overrides[get_session] = get_session_override
    yield TestClient(app)

    app.dependency_overrides.clear()
