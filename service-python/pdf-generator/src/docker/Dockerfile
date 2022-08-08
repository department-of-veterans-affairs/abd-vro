ARG WKHTML_VERSION=3.9.9-0.12.6-small
FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN mkdir -p /app
WORKDIR /app

COPY . /app/

RUN pip install --no-cache-dir -r requirements.txt
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
CMD ["python", "-u", "main_consumer.py"]
