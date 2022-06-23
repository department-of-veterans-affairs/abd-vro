ARG WKHTML_VERSION=3.9.9-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Create a directory for the app code
RUN mkdir -p /app
WORKDIR /app
# build.gradle copies src/ files into build/docker, where this Dockerfile will be built
COPY . /app/

RUN pip install --no-cache -r requirements.txt

CMD ["python", "app.py"]