from .pdf_generator import PDFGenerator
from .settings import pdf_options

if __name__ == '__main__':
    diagnosis_name = "hypertension-v2-weasyprint"
    message = {"pdfLibrary": "weasyprint"}
    pdf_generator = PDFGenerator(pdf_options, message)
    variables = pdf_generator.generate_template_variables(diagnosis_name, message)
    # print("Variables: ", variables)
    template = pdf_generator.generate_template_file(diagnosis_name, variables, test_mode=True, loader="local_pdf_test")
    # print("Template: ", template)
    with open("test.html", "w") as file:
        file.write(template)
    # WKHMTMLTOPDF
    # pdf = pdf_generator.generate_pdf_from_string(diagnosis_name, template, variables, 'test.pdf')
    # WEASYPRINT
    pdf = pdf_generator.generate_pdf_from_string(diagnosis_name, template, variables)
    with open("test.pdf", "wb") as file:
        file.write(pdf)
