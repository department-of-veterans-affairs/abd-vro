from vro_streamlit.auth.user import User


def log_in() -> User:
    return User('test')


def log_out() -> bool:
    return True
