ARG WKHTML_VERSION=3.10.6-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# RUN apk update && apk --no-cache add expat=2.4.9-r0

RUN adduser --disabled-password docker
USER docker

WORKDIR /home/docker

COPY --chown=docker:docker . /home/docker/

RUN pip install --no-cache-dir --user -r requirements.txt

CMD ["python", "-u", "main_consumer.py"]
