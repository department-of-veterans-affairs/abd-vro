from src.python_src.util.lookup_table import DropdownLookupTable

DROPDOWN_LUT_SIZE = 489


def test_build_dropdown_lookup_table():
    lookup_table = DropdownLookupTable()
    assert len(lookup_table) == DROPDOWN_LUT_SIZE
