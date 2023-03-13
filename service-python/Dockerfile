ARG WKHTML_VERSION=3.10.6-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=5 CMD [ "CMD-SHELL", "pgrep python || exit 1" ]

# Used to specify the service folder you want to build if running locally
# No longer write *.pyc files to disk
ENV PYTHONDONTWRITEBYTECODE 1
# Send logs directly to container
ENV PYTHONUNBUFFERED 1

RUN apk update && apk --no-cache add expat=2.5.0-r0 && mkdir -p /home/docker && mkdir -p /home/docker/shared
RUN adduser --disabled-password docker && chown -hR docker:docker /home/docker/

COPY --chown=docker:docker . /home/docker/
COPY --chown=docker:docker ../shared /home/docker/shared
WORKDIR /home/docker
RUN pip install --no-cache-dir -r requirements.txt

USER docker
CMD ["python", "-u", "main_consumer.py"]
