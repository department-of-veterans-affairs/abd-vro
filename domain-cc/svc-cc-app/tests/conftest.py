""" Pytest configuration. This file is automatically loaded by pytest before any tests. """

import pytest
from fastapi.testclient import TestClient

from src.api import app


@pytest.fixture
def client() -> TestClient:
    return TestClient(app)
