""" Pytest configuration. This file is automatically loaded by pytest before any tests. """

import pytest
from fastapi.testclient import TestClient
from src.python_src.api import app
from src.python_src.db.session import SessionLocal
from typing import Generator


@pytest.fixture(scope="session")
def db() -> Generator:
    yield SessionLocal()


@pytest.fixture
def client() -> TestClient:
    return TestClient(app)
