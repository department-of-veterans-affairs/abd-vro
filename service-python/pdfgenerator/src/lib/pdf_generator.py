import json
import os
from datetime import datetime

import pdfkit
import pytz
from dateutil import parser
from dateutil.relativedelta import relativedelta
from jinja2 import Environment, PackageLoader, select_autoescape

lib_dir = os.path.dirname(__file__)


class PDFGenerator:

    def __init__(self, options):
        self.options = options

    def generate_template_variables(self, template_name: str, pdf_data: dict) -> dict:
        placeholder_variables = json.load(open(os.path.join(lib_dir, f"template_variables/{template_name}.json")))
        filled_variables = {key: pdf_data.get(key, placeholder_variables[key]) for key in placeholder_variables}
        # Starting date from when the data is fetched. Mainly to be used to display a range Ex: (start_date) to (timestamp)
        filled_variables["start_date"] = datetime.now() - relativedelta(years=1)
        filled_variables["timestamp"] = pytz.utc.localize(datetime.now())
        for medication_info in filled_variables["evidence"]["medications"]:
            medication_info["authoredOn"] = parser.parse(medication_info["authoredOn"])
        return filled_variables

    def generate_template_file(self, template_name: str, template_variables: dict, test_mode=False) -> str:
        loader_path = "pdfgenerator.src.lib" if test_mode else "lib"
        jinja_env = Environment(
            loader=PackageLoader(loader_path),
            autoescape=select_autoescape()
        )
        template = jinja_env.get_template(f"{template_name}.html")
        generated_html = template.render(**template_variables)

        return generated_html

    def generate_pdf_from_string(self, html: str) -> bytes or bool:
        return pdfkit.from_string(html, False, options=self.options)
