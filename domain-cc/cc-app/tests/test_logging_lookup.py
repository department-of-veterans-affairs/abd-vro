from src.python_src.util.logging_dropdown_selections import build_logging_table


def test_build_dropdown_options_list():
    assert len(build_logging_table()) == 580


def test_new_value_in_list():
    test_value = "astragalectomy or talectomy (removal of talus bone in ankle), right"
    assert test_value in build_logging_table()


def test_previous_value_not_in_list():
    test_value = "migraine"
    assert test_value not in build_logging_table()


def test_previous_value_not_in_v3_list():
    test_value = "urticaria (hives)"
    assert test_value not in build_logging_table()


def test_kidney_cancer():
    v2_kidney_cancer_value = "kidney cancer (renal cancer)"
    v3_kidney_cancer_values = [
        "kidney cancer (renal cancer), bilateral",
        "kidney cancer (renal cancer), left",
        "kidney cancer (renal cancer), right",
    ]
    logging_table = build_logging_table()

    assert v2_kidney_cancer_value not in logging_table
    for v3_value in v3_kidney_cancer_values:
        assert v3_value in logging_table
