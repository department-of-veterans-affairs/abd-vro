# from lib.pdf_generator import PDFGenerator
# from lib.settings import pdf_options
#
# if __name__ == '__main__':
#     diagnosis_name = "hypertension-v2"
#     message = {}
#     pdf_generator = PDFGenerator(pdf_options)
#     variables = pdf_generator.generate_template_variables(diagnosis_name, message)
#     # print("Variables: ", variables)
#     template = pdf_generator.generate_template_file(diagnosis_name, variables, test_mode=True, loader="lib")
#     # print("Template: ", template)
#     with open("test.html", "w") as file:
#         file.write(template)
#     pdf = pdf_generator.generate_pdf_from_string(diagnosis_name, template, variables, 'test.pdf')
#     # print("PDF: ", pdf)
