ARG WKHTML_VERSION=3.10.6-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

# Used to specify the service folder you want to build if running locally
# No longer write *.pyc files to disk
ENV PYTHONDONTWRITEBYTECODE 1
# Send logs directly to container
ENV PYTHONUNBUFFERED 1

RUN apk update && apk --no-cache add expat=2.5.0-r0 py3-pip=22.1.1-r0 gcc=11.2.1_git20220219-r2 musl-dev=1.2.3-r2 python3-dev=3.10.9-r0 pango=1.50.7-r0 zlib-dev=1.2.12-r3 jpeg-dev=9d-r1 openjpeg-dev=2.5.0-r0 g++=11.2.1_git20220219-r2 libffi-dev=3.4.2-r1 && mkdir -p /home/docker
RUN adduser --disabled-password docker && chown -hR docker:docker /home/docker/

# SERVICE_SRC_FOLDER is used by secrel/config.yml
ARG SERVICE_SRC_FOLDER=.
COPY --chown=docker:docker ${SERVICE_SRC_FOLDER} /home/docker/
WORKDIR /home/docker
RUN pip install --no-cache-dir -r requirements.txt

USER docker
CMD ["python", "-u", "main_consumer.py"]
