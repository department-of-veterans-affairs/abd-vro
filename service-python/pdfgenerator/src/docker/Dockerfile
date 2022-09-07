ARG WKHTML_VERSION=3.9.9-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN adduser --disabled-password python
USER python

WORKDIR /home/python

COPY --chown=python:python . /home/python/

RUN pip install --no-cache-dir --user -r requirements.txt

CMD ["python", "-u", "main_consumer.py"]
