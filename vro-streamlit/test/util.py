def assert_markdown_contains_values(markdown, values: str | list[str]):
    if isinstance(values, str):
        values = [values]
    markdown_text = [md.value for md in markdown]
    for val in values:
        assert val in markdown_text, f"Expected value '{val}' not found in markdown text '{markdown_text}'"


def assert_markdown_contains_all_values(markdown, values: list[str]):
    markdown_text = [md.value for md in markdown]
    assert markdown_text == values, f"Expected values '{values}' not found in markdown text '{markdown_text}'"


def assert_button_contains_label(button, value):
    assert button.label == value, f"Expected value '{value}' not found in button '{button}'"
