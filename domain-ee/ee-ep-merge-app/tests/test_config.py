from config import create_sqlalchemy_db_uri


def test_no_postgres_url(monkeypatch):
    monkeypatch.setenv("POSTGRES_USER", "user")
    monkeypatch.setenv("POSTGRES_PASSWORD", "password")
    monkeypatch.setenv("POSTGRES_HOST", "localhost")
    monkeypatch.setenv("POSTGRES_PORT", "5432")
    monkeypatch.setenv("POSTGRES_DB", "test")
    uri = create_sqlalchemy_db_uri()
    assert uri == "postgresql://user:password@localhost:5432/test"


def test_no_postgres_url_with_defaults():
    uri = create_sqlalchemy_db_uri()
    assert uri == "postgresql://vro_user:vro_user_pw@localhost:5432/vro"


def test_postgres_url_has_no_user_password(monkeypatch):
    monkeypatch.setenv("POSTGRES_URL", "postgresql://thisisatest.com/test")
    monkeypatch.setenv("POSTGRES_USER", "user")
    monkeypatch.setenv("POSTGRES_PASSWORD", "password")
    uri = create_sqlalchemy_db_uri()
    assert uri == "postgresql://user:password@thisisatest.com/test"


def test_postgres_url_has_user_password(monkeypatch):
    monkeypatch.setenv("POSTGRES_URL", "postgresql://allowedUser:madethisup@thisisatest.com/test")
    monkeypatch.setenv("POSTGRES_USER", "user")
    monkeypatch.setenv("POSTGRES_PASSWORD", "password")
    uri = create_sqlalchemy_db_uri()
    assert uri == "postgresql://allowedUser:madethisup@thisisatest.com/test"
