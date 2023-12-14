from src.python_src.util.logging_dropdown_selections import build_logging_table


def test_build_dropdown_options_list():
    assert len(build_logging_table()) == 529


def test_new_value_in_list():
    test_value = "astragalectomy or talectomy (removal of talus bone in ankle), right"
    assert test_value in build_logging_table()


def test_previous_value_not_in_list():
    test_value = "migraine"
    assert test_value not in build_logging_table()
