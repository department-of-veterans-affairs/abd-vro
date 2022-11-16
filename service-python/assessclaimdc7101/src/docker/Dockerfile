ARG PYTHON_VERSION=3.9

FROM python:${PYTHON_VERSION}-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN apk update && apk --no-cache add expat=2.5.0-r0 && mkdir -p /home/docker
COPY . /home/docker/
RUN adduser --disabled-password docker && chown -R 777:777 /home/docker/
WORKDIR /home/docker
RUN pip install --no-cache-dir -r requirements.txt
USER docker
CMD ["python", "-u", "main_consumer.py"]

