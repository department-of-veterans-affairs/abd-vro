from enum import Enum


class StrEnum(str, Enum):
    """
    StrEnum subclasses enum.Enum and overrides `enum.auto()` to have values equal to their names.
    Enums inheriting from this class that set values using `enum.auto()` will have values equal to their names.
    """

    # noinspection PyMethodParameters
    def _generate_next_value_(name, start, count, last_values) -> str:  # type: ignore
        """
        Uses the name as the automatic value, rather than an integer

        See https://docs.python.org/3.10/library/enum.html#using-automatic-values for reference
        """
        return name
