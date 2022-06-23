import json
import os

import pdfkit
from jinja2 import Environment, PackageLoader, select_autoescape
from config.pdf import available_templates

lib_dir = os.path.dirname(__file__)


class PDFGenerator:

	def __init__(self, options):
		self.options = options


	def get_template_name(self, template_type: str) -> str:
		selected_template = available_templates[template_type]
		return selected_template


	def generate_template_file(self, template_name: str, pdf_data) -> str:
		jinja_env = Environment(
			loader=PackageLoader("app"),
			autoescape=select_autoescape()
		)

		template_file = self.get_template_name(template_name)
		placeholder_variables = json.load(open(os.path.join(lib_dir, f"pdf_variables/{template_file}.json")))
		filled_variables = {key: pdf_data.get(key, placeholder_variables[key]) for key in placeholder_variables}
		template = jinja_env.get_template(f"{template_file}.html")
		generated_html = template.render(**filled_variables)

		return generated_html


	def generate_pdf_from_string(self, html: str) -> bytes:
		# config = pdfkit.configuration(wkhtmltopdf='/opt/bin/wkhtmltopdf')

		return pdfkit.from_string(html, False, options=self.options)
