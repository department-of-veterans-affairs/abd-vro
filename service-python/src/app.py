from lib.consumer import RabbitMQConsumer
from config.pdf import consumer_config

consumer = RabbitMQConsumer(consumer_config)
consumer.setup()
# import base64

# from flask import Flask, request
# from lib.pdf_generator import generate_pdf_from_string, generate_template_file

# app = Flask(__name__)


# @app.route("/", methods=["POST"])
# def pdf_generator():
#     print(request)
#     request_json = request.get_json()
#     print(request_json)
#     pdf_type = request_json["pdf_type"]

#     template = generate_template_file(f"{pdf_type}", request_json)
#     pdf = generate_pdf_from_string(template)
#     print("PDF Base 64:", base64.b64encode(pdf))
#     return {"pdf": base64.b64encode(pdf).decode("utf-8")}


# if __name__ == "__main__":
#   app.run(host="0.0.0.0", port=5000)