from datetime import datetime
from dateutil.relativedelta import relativedelta
import json
import os

import pdfkit
from jinja2 import Environment, PackageLoader, select_autoescape
from config.settings import available_templates

lib_dir = os.path.dirname(__file__)


class PDFGenerator:

	def __init__(self, options):
		self.options = options


	def get_template_name(self, template_type: str) -> str:
		selected_template = available_templates[template_type]
		return selected_template


	def generate_template_file(self, template_name: str, pdf_data) -> str:
		jinja_env = Environment(
			loader=PackageLoader("lib"),
			autoescape=select_autoescape()
		)

		template_file = self.get_template_name(template_name)
		placeholder_variables = json.load(open(os.path.join(lib_dir, f"template_variables/{template_file}.json")))
		filled_variables = {key: pdf_data.get(key, placeholder_variables[key]) for key in placeholder_variables}
		filled_variables["timestamp"] = datetime.now()
		# Starting date from when the data is fetched. Mainly to be used to display a range Ex: (start_date) to (timestamp) 
		filled_variables["start_date"] = datetime.now() - relativedelta(years=1) 
		template = jinja_env.get_template(f"{template_file}.html")
		generated_html = template.render(**filled_variables)

		return generated_html


	def generate_pdf_from_string(self, html: str, save_locally: bool) -> bytes or bool:
		# config = pdfkit.configuration(wkhtmltopdf='/opt/bin/wkhtmltopdf')
		if save_locally:
			examples_path = os.path.abspath("./test.pdf")
			return pdfkit.from_string(html, examples_path, options=self.options)
		return pdfkit.from_string(html, False, options=self.options)
