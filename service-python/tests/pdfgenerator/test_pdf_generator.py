import json
import os

from pdfgenerator.src.lib.pdf_generator import PDFGenerator

lib_dir = os.path.dirname(__file__)


def test_default_template_variables():
    pdf_generator = PDFGenerator({})
    template = "asthma"
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
    generated_variables["veteran_info"]["birthdate"] = default_variables["veteran_info"]["birthdate"]

    assert default_variables == generated_variables


def test_replaced_template_variables():
    pdf_generator = PDFGenerator({})
    template = "asthma"

    first_name = "test"
    rabbitmq_data = {"veteran_info": {"first": first_name, "birthdate": "1935-06-15T00:00:00+00:00"}}
    generated_variables = pdf_generator.generate_template_variables(
        template, rabbitmq_data
    )

    assert generated_variables["veteran_info"]["first"] == first_name


def test_asthma_generate_html_file():
    pdf_generator = PDFGenerator({})
    template = "asthma"

    generated_variables = pdf_generator.generate_template_variables(template, {})
    html_file = pdf_generator.generate_template_file(
        template, generated_variables, True
    )

    document_title = "Asthma Rapid Ready for Decision | Claim for Increase"
    assert document_title in html_file


def test_asthma_valid_variables_in_html_file():
    pdf_generator = PDFGenerator({})
    template = "asthma"

    first_name = "test"
    rabbitmq_data = {"veteran_info": {"first": first_name, "birthdate": "1935-06-15T00:00:00+00:00"}}
    generated_variables = pdf_generator.generate_template_variables(
        template, rabbitmq_data
    )
    html_file = pdf_generator.generate_template_file(
        template, generated_variables, True
    )

    assert first_name in html_file


def test_hypertension_generate_html_file():
    pdf_generator = PDFGenerator({})
    template = "hypertension"

    generated_variables = pdf_generator.generate_template_variables(template, {})
    html_file = pdf_generator.generate_template_file(
        template, generated_variables, True
    )

    document_title = (
        "Hypertension Rapid Ready for Decision | Claim for Increase"
    )
    assert document_title in html_file
