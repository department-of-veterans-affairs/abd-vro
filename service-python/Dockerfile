ARG WKHTML_VERSION=3.10.6-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

# Used to specify the service folder you want to build if running locally
# No longer write *.pyc files to disk
ENV PYTHONDONTWRITEBYTECODE 1
# Send logs directly to container
ENV PYTHONUNBUFFERED 1

RUN apk update && apk --no-cache add expat=2.5.0-r0 py3-pip gcc musl-dev python3-dev pango zlib-dev jpeg-dev openjpeg-dev g++ libffi-dev && mkdir -p /home/docker
RUN adduser --disabled-password docker && chown -hR docker:docker /home/docker/

# SERVICE_SRC_FOLDER is used by secrel/config.yml
ARG SERVICE_SRC_FOLDER=.
COPY --chown=docker:docker ${SERVICE_SRC_FOLDER} /home/docker/
WORKDIR /home/docker
RUN pip install --no-cache-dir -r requirements.txt

USER docker
CMD ["python", "-u", "main_consumer.py"]
