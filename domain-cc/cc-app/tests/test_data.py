from src.python_src.util.lookup_table import (
    ContentionTextLookupTable,
    DiagnosticCodeLookupTable,
)

CONTENTION_DROPDOWN_LUT_SIZE = 1056
DIAGNOSTIC_CODE_LUT_SIZE = 755


def test_build_dropdown_lookup_table():
    lookup_table = ContentionTextLookupTable()
    assert len(lookup_table) == CONTENTION_DROPDOWN_LUT_SIZE


def test_build_dc_lut():
    dc_lookup_table = DiagnosticCodeLookupTable()
    assert len(dc_lookup_table) == DIAGNOSTIC_CODE_LUT_SIZE
