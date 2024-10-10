from app.app import main


def test_main() -> None:
    env, debug_flag = main()
    assert env == 'local'
    assert debug_flag  # from pyproject DEBUG env block.
