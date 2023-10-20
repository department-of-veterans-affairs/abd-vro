""" Pytest configuration. This file is automatically loaded by pytest before any tests. """

import pytest
from fastapi.testclient import TestClient
from src.python_src.database import get_db
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool
from src.python_src.database_models import Base

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

# engine = create_engine(
#     SQLALCHEMY_DATABASE_URL,
#     connect_args={"check_same_thread": False},
#     poolclass=StaticPool,
# )
# TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
#
# print('got here to the bind thingy')
# Base.metadata.create_all(bind=engine)
#
# def override_get_db():
#     try:
#         db = TestingSessionLocal()
#         yield db
#     finally:
#         db.close()
#
from src.python_src.api import app
# app.dependency_overrides[get_db] = override_get_db


@pytest.fixture
def client() -> TestClient:
    return TestClient(app)
