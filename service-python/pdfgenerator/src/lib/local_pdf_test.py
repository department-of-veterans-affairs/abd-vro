from pdf_generator import PDFGenerator
from settings import pdf_options

if __name__ == '__main__':
    diagnosis_name = "summary"
    message = {}
    pdf_generator = PDFGenerator(pdf_options)
    variables = pdf_generator.generate_template_variables(diagnosis_name, message)
    # print("Variables: ", variables)
    template = pdf_generator.generate_template_file(diagnosis_name, variables, test_mode=True, loader="local_pdf_test")
    print("Template: ", template)
    with open("template_test.html", "w") as file:
      file.write(template)
    pdf = pdf_generator.generate_pdf_from_string(diagnosis_name, template)
    # print("PDF: ", pdf)