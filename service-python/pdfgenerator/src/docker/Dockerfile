ARG WKHTML_VERSION=3.10.6-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN mkdir -p /app
WORKDIR /app

COPY . /app/

RUN pip install --no-cache-dir -r requirements.txt

CMD ["python", "-u", "main_consumer.py"]
