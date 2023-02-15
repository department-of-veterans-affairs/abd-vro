import json
import os
from datetime import datetime
from unittest.mock import patch

import pytest

from pdfgenerator.src.lib import settings
from pdfgenerator.src.lib.pdf_generator import PDFGenerator

lib_dir = os.path.dirname(__file__)


@pytest.mark.parametrize("template", ["asthma-v1"])
def test_default_template_variables(template):
    """Test if default values all get added into the template."""
    pdf_generator = PDFGenerator({})

    default_variables = json.load(
        open(
            os.path.join(
                lib_dir,
                f"../../pdfgenerator/src/lib/template_variables/{template}.json",
            )
        )
    )

    generated_variables = pdf_generator.generate_template_variables(template, {})

    # these variables are only available when the pdf_generator is called so no need to compare
    del generated_variables["timestamp"]
    del generated_variables["start_date"]

    # reset this field because it gets turned into a datetime object so it wont match
    generated_variables["veteranInfo"]["birthdate"] = default_variables["veteranInfo"]["birthdate"]

    # reset this field because it gets turned into a datetime object so it wont match
    generated_variables["veteranInfo"]["birthdate"] = default_variables["veteranInfo"]["birthdate"]

    assert default_variables == generated_variables


@pytest.mark.parametrize("template", ["asthma-v1"])
def test_replaced_template_variables(template):
    """Test if the default values get replaced."""
    pdf_generator = PDFGenerator({})

    first_name = "test"
    rabbitmq_data = {"veteranInfo": {"first": first_name, "birthdate": "1935-06-15T00:00:00+00:00"}}
    generated_variables = pdf_generator.generate_template_variables(
        template, rabbitmq_data
    )

    assert generated_variables["veteranInfo"]["first"] == first_name


@pytest.mark.parametrize("template", ["asthma-v1"])
def test_generate_html_file(template):
    """Test if the PDF HTML file gets generated."""
    pdf_generator = PDFGenerator({})

    generated_variables = pdf_generator.generate_template_variables(template, {})
    html_file = pdf_generator.generate_template_file(
        template, generated_variables, True
    )

    document_title = "Rapid Ready for Decision | Claim for Increase"
    assert document_title in html_file


@pytest.mark.parametrize("template", ["asthma-v1"])
def test_valid_variables_in_html_file(template):
    """Test that the replaced variable appears in the HTML file."""
    pdf_generator = PDFGenerator({})

    first_name = "test"
    rabbitmq_data = {"veteranInfo": {"first": first_name, "birthdate": "1935-06-15T00:00:00+00:00"}}
    generated_variables = pdf_generator.generate_template_variables(
        template, rabbitmq_data
    )
    html_file = pdf_generator.generate_template_file(
        template, generated_variables, True
    )

    assert first_name in html_file


@pytest.mark.parametrize("template", ["asthma-v1"])
def test_medication_date_conversion(template):
    """Test if 'authoredOn' in 'medications' is a datetime."""
    pdf_generator = PDFGenerator({})

    rabbitmq_data = {"evidence": {"medications": [{"authoredOn": "1935-06-15T00:00:00+00:00"}]}}
    generated_variables = pdf_generator.generate_template_variables(
        template, rabbitmq_data
    )

    selected_date = generated_variables["evidence"]["medications"][0]["authoredOn"]

    assert isinstance(selected_date, datetime)


@patch("pdfkit.from_string")
@pytest.mark.parametrize("template", ["asthma-v1"])
def test_pdf_generation(pdfkit_mock, template):
    """Test if the generate PDF function gets called."""
    pdf_generator = PDFGenerator({})

    rabbitmq_data = {"veteranInfo": {"birthdate": "1935-06-15T00:00:00+00:00Z"}}
    generated_variables = pdf_generator.generate_template_variables(
        template, rabbitmq_data
    )
    html_file = pdf_generator.generate_template_file(
        template, generated_variables, True
    )
    tag = (
        "<html"
    )
    pdf_generator.generate_pdf_from_string(
        template, html_file, {}
    )

    assert tag in html_file
    assert pdfkit_mock.called
