import json
import logging
import os

import pdfkit
from jinja2 import Environment, PackageLoader, select_autoescape

from .helper_functions import *  # noqa: F403

lib_dir = os.path.dirname(__file__)


class PDFGenerator:

    def __init__(self, options):
        self.options = options

    def generate_template_variables(self, template_name: str, pdf_data: dict) -> dict:
        placeholder_variables = json.load(open(os.path.join(lib_dir, f"template_variables/{template_name}.json")))
        filled_variables = {key: pdf_data.get(key, placeholder_variables[key]) for key in placeholder_variables}
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
        base_toc_file_path = os.path.join(lib_dir, f"templates/{template_name}/base_toc.xsl")
        css_file_path = os.path.join(lib_dir, f"templates/{template_name}/base.css")
        # If the CSS file does not exist for the template, use the one in the shared folder
        if not os.path.isfile(css_file_path):
            css_file_path = os.path.join(lib_dir, "templates/shared/base.css")

        if os.path.isfile(base_toc_file_path):
            # Call a helper function that make adjustments to toc before creating
            generated_toc_file_path = toc_helper_all(base_toc_file_path, data) # noqa: F405, E261
            toc = {'xsl-style-sheet': generated_toc_file_path}
            return pdfkit.from_string(html, output, css=css_file_path, options=self.options, toc=toc, verbose=True)
        else:
            return pdfkit.from_string(html, output, css=css_file_path, options=self.options, verbose=False)
