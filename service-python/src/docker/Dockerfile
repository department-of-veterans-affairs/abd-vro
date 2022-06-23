ARG WKHTML_VERSION=3.9.9-0.12.6-small

FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION}

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

WORKDIR /app

COPY requirements.txt requirements.txt

RUN pip install --no-cache -r requirements.txt

COPY . .

CMD ["flask", "run", "--host", "0.0.0.0"]