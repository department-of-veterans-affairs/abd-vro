ARG PYTHON_VERSION=3.8

FROM python:${PYTHON_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN adduser --disabled-password docker
USER docker

WORKDIR /home/docker

COPY --chown=docker:docker . /home/docker/

RUN pip install --no-cache-dir --user -r requirements.txt

CMD ["python", "-u", "main_consumer.py"]
#https://stackoverflow.com/a/55954143
