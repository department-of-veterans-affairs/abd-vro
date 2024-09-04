# Max Claim for Increase (CFI) API

`/track/claim` track a claim by events

## Getting started

Install Python3.10
If you're on a Mac, you can use pyenv to handle multiple python versions

```
brew install pyenv
pyenv install python3.10
pyenv global python3.10 # or don't do this if you want a different version available globally for your system
```

Create a virtual env:

```
python -m venv ~/.virtualenvs/svc-claim-tracker # or wherever you want
source ~/.virtualenvs/svc-claim-tracker/bin/activate
```

Make sure your python path is set up to pull from your virtualenv:

```
which python3
# /Users/<your_username>/.virtualenvs/svc-claim-tracker/bin/python
```

Install dependencies and run webserver

```
cd domain-bie-events/svc-claim-tracker/src
pip install -r requirements.txt
pip install -e .
uvicorn api:app --port 8150 --reload
```

## Testing it all together

Run the Python webserver (uvicorn command above). Now you should be able to make a post request to the `track/claim`
endpoint with a request body of the format:

```
{
    "claim_id": "1",
    "established_time": "2024-01-01T00:00:00-06:00",
    "features": [
      {
        "name": "feature_under_test",
        "enabled": true,
      }
    ]
}
```

This should result in a response with the following body:

```

```


## Unit tests

Make sure you're in your `.virtualenv`:

Navigate to `domain-bie-events/svc-claim-tracker`:

```
cd domain-bie-events/svc-claim-tracker
```

Run the tests:

```
pytest
```

## Contributing

### Install dev dependencies

```
source ~/.virtualenvs/svc-claim-tracker/bin/activate
pip install -r dev-requirements.txt
# MAKE SURE you are in adb-vro/domain-bie-events to get the right pre-commit-config.yaml installed
pre-commit install
```

## Building docs

```
source ~/.virtualenvs/svc-claim-tracker/bin/activate
cd ./domain-bie-events/svc-claim-tracker
python3 src/python_src/pull_api_documentation.py
```

## Docker Stuff

### Build the image

Follow steps for
[Platform Base + API-Gateway](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Docker-Compose#platform-base)
then run the svc-claim-tracker with the following command from the `abd_vro directory`:

```
COMPOSE_PROFILES="all" ./gradlew :domain-bie-events:dockerComposeUp
```

### Verify API in API Gateway

Navigate to [Swagger](http://localhost:8060/webjars/swagger-ui/index.html?urls.primaryName=3.%20Max%20CFI%20API)

Try to send a request on the post endpoint with the following request body:

```
{
    "claim_id": "1",
    "established_time": "2024-01-01T00:00:00-06:00",
    "features": [
      {
        "name": "feature_under_test",
        "enabled": true,
      }
    ]
}
```
