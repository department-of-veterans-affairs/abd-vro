from streamlit.testing.v1 import AppTest

def test_main() -> None:
    app = AppTest.from_file('src/app/main.py')
    app.run()
    assert not app.exception
