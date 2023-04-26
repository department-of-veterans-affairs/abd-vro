import io
import json
import logging
import os

import pdfkit
from jinja2 import Environment, PackageLoader, select_autoescape
from weasyprint import HTML

from .helper_functions import *  # noqa: F403

lib_dir = os.path.dirname(__file__)


class PDFGenerator:

    def __init__(self, options, message_data=None):
        self.options = options
        if message_data and "pdfLibrary" in message_data and message_data["pdfLibrary"].lower() in ["wkhtmltopdf", "weasyprint"]:
            self.library = message_data["pdfLibrary"]
        else:
            self.library = "wkhtmltopdf"
        logging.info(f"Using PDF Library: {self.library}")

    def generate_template_variables(self, template_name: str, pdf_data: dict) -> dict:
        placeholder_variables = json.load(open(os.path.join(lib_dir, f"template_variables/{template_name}.json")))
        filled_variables = {key: pdf_data.get(key, placeholder_variables[key]) for key in placeholder_variables}
        if template_name == "default":
            filled_variables = {"default_data": pdf_data}
        # Run the helper function for the specific code if it exists
        try:
            eval(f"pdf_helper_{filled_variables['document_type']}(filled_variables)")
        except: # noqa: E722, E261
            logging.info("No helper function found")
        # Call a helper function that gets run for all codes
        filled_variables = pdf_helper_all(filled_variables) # noqa: F405, E261
        return filled_variables

    def generate_template_file(self, template_name: str, template_variables: dict, test_mode=False, loader="pdfgenerator.src.lib") -> str:
        loader_path = loader if test_mode else "lib"
        jinja_env = Environment(
            loader=PackageLoader(loader_path),
            autoescape=select_autoescape()
        )
        template = jinja_env.get_template(f"{template_name}.html")
        generated_html = template.render(**template_variables)

        return generated_html

    def generate_pdf_from_string(self, template_name: str, html: str, data, output=False) -> bytes or bool:
        if self.library == "wkhtmltopdf":
            base_toc_file_path = os.path.join(lib_dir, f"templates/{template_name}/base_toc.xsl")
            if os.path.isfile(base_toc_file_path):
                # Call a helper function that make adjustments to toc before creating
                generated_toc_file_path = toc_helper_all(base_toc_file_path, data) # noqa: F405, E261
                toc = {'xsl-style-sheet': generated_toc_file_path}
                return pdfkit.from_string(html, output, options=self.options, toc=toc, verbose=False)
            else:
                return pdfkit.from_string(html, output, options=self.options, verbose=False)
        else:
            buffer = io.BytesIO()
            html_obj = HTML(string=html)
            if output:
                html_obj.write_pdf(output)
                return
            else:
                html_obj.write_pdf(buffer)
                return buffer.getbuffer()
