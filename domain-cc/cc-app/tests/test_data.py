from src.python_src.util.lookup_table import ConditionDropdownLookupTable

DROPDOWN_LUT_SIZE = 1050


def test_build_dropdown_lookup_table():
    lookup_table = ConditionDropdownLookupTable()
    assert len(lookup_table) == DROPDOWN_LUT_SIZE
