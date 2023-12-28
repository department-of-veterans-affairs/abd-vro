""" Pytest configuration. This file is automatically loaded by pytest before any tests. """
import uuid
from unittest.mock import MagicMock

import pytest
from fastapi.testclient import TestClient
from src.python_src.api import app
from src.python_src.db.database import Database
from src.python_src.schema import merge_job as schema


@pytest.fixture
def client() -> TestClient:
    return TestClient(app)


@pytest.fixture
def db():
    return MagicMock(spec=Database)


@pytest.fixture
def merge_job():
    return schema.MergeJob(job_id=uuid.uuid4(), pending_claim_id=1, ep400_claim_id=2, state="PENDING")
