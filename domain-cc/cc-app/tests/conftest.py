""" Pytest configuration. This file is automatically loaded by pytest before any tests. """

import pytest
from fastapi.testclient import TestClient
from src.python_src.api import app

TUBERCULOSIS_CLASSIFICATION = {
    "diagnostic_code": 7710,
    "classification_code": 6890,
    "classification_name": "Tuberculosis",
}
BENIGN_GROWTH_BRAIN_CLASSIFICATION = {
    "diagnostic_code": 8003,
    "classification_code": 8964,
    "classification_name": "Cyst/Benign Growth - Neurological other System",
}
DRUG_INDUCED_PULMONARY_PNEMONIA_CLASSIFICATION = {
    "diagnostic_code": 6829,
    "classification_code": 9012,
    "classification_name": "Respiratory",
}

SQLALCHEMY_DATABASE_URL = "sqlite://"


@pytest.fixture
def client() -> TestClient:
    return TestClient(app)
