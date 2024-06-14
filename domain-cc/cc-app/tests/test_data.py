from src.python_src.util.lookup_table import ContentionTextLookupTable

DROPDOWN_LUT_SIZE = 1050


def test_build_dropdown_lookup_table():
    lookup_table = ContentionTextLookupTable()
    assert len(lookup_table) == DROPDOWN_LUT_SIZE
