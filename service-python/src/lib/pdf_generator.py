from datetime import datetime
from dateutil.relativedelta import relativedelta
import json
import os

import pdfkit
from jinja2 import Environment, PackageLoader, select_autoescape

lib_dir = os.path.dirname(__file__)


class PDFGenerator:

	def __init__(self, options):
		self.options = options

	def generate_template_variables(self, template_name: str, pdf_data: dict) -> dict:
		placeholder_variables = json.load(open(os.path.join(lib_dir, f"template_variables/{template_name}.json")))
		filled_variables = {key: pdf_data.get(key, placeholder_variables[key]) for key in placeholder_variables}
		filled_variables["timestamp"] = datetime.now()
		# Starting date from when the data is fetched. Mainly to be used to display a range Ex: (start_date) to (timestamp) 
		filled_variables["start_date"] = datetime.now() - relativedelta(years=1)
		return filled_variables

	def generate_template_file(self, template_name: str, template_variables: dict) -> str:
		jinja_env = Environment(
			loader=PackageLoader("lib"),
			autoescape=select_autoescape()
		)
		template = jinja_env.get_template(f"{template_name}.html")
		generated_html = template.render(**template_variables)

		return generated_html


	def generate_pdf_from_string(self, html: str) -> bytes or bool:
		return pdfkit.from_string(html, False, options=self.options)
