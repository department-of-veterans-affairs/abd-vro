from lib.pdf_generator import PDFGenerator
from lib.settings import pdf_options

if __name__ == '__main__':
    diagnosis_name = "hypertension-v2-cell-merge"
    message = {
        "evidence": {
            "bp_readings": [],
            "conditions": [],
            "medications": [
                {"receiptDate": "1/2/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "2/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "2/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "2/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "2/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "3/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "4/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "5/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "5/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "5/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "6/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "7/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},
                {"receiptDate": "7/1/11", "description": "med1", "dateFormatted": "1/1/11", "organization": "org1", "page": "1", "document": "doc1"},

            ],
            "procedures": [],
            "serviceLocations": [],
            "documentsWithoutAnnotationsChecked": []
        }
    }
    pdf_generator = PDFGenerator(pdf_options)
    variables = pdf_generator.generate_template_variables(diagnosis_name, message)
    # print("Variables: ", variables)
    template = pdf_generator.generate_template_file(diagnosis_name, variables, test_mode=True, loader="lib")
    # print("Template: ", template)
    with open("test.html", "w") as file:
        file.write(template)
    pdf = pdf_generator.generate_pdf_from_string(diagnosis_name, template, variables, 'test.pdf')
    # print("PDF: ", pdf)
