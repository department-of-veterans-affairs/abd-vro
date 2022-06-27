ARG PYTHON_VERSION=3.8

FROM python:${PYTHON_VERSION}

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN mkdir -p /app
WORKDIR /app

COPY . /app/

COPY requirements.txt requirements.txt

RUN pip install --no-cache -r requirements.txt

CMD ["python", "-u", "app.py"]
#https://stackoverflow.com/a/55954143
