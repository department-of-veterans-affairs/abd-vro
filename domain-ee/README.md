# Max Claim for Increase (CFI) API

`/max-ratings` maps a list of disabilities to their max ratings, if any.

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
python -m venv ~/.virtualenvs/domain-ee # or wherever you want
source ~/.virtualenvs/domain-ee/bin/activate
```

Make sure your python path is set up to pull from your virtualenv:

```
which python3
# /Users/<your_username>/.virtualenvs/domain-ee/bin/python
```

Install dependencies and run webserver

```
cd domain-ee/max-cfi-app/src
pip3 install -r requirements.txt
uvicorn api:app --port 8130 --reload
```

## Testing it all together

Run the Python webserver (uvicorn command above). Now you should be able to make a post request to the `cfi/max-ratings`
endpoint with a request body of the format:

```
{
    "diagnostic_codes": [
        6260
    ]
}
```

This should result in a response with the following body:

```
{
    "ratings": [
        {
            "diagnostic_code": 6260,
            "max_rating": 0.1
        }
    ]
}
```

### Notes on usage:

* Each diagnostic code in the request should yield an item in the ratings array of the response body.
* If any of the diagnostic codes are not found, the response will yield a `404` status code.
* An invalid request such as missing/invalid field will result in `422` status code.
* Duplicate entries in the diagnostic codes array will yield a single result in the ratings array corresponding to that
  diagnostic code.

## Unit tests

Make sure you're in your `.virtualenv`:

```
source ~/.virtualenvs/domain-ee/bin/activate
```

Navigate to `max-cfi-app/`:

```
cd domain-ee/max-cfi-app
```

Run the tests:

```
pytest
```

## Contributing

### Install dev dependencies

```
source ~/.virtualenvs/domain-ee/bin/activate
pip install -r dev-requirements.txt
# MAKE SURE you are in adb-vro/domain-ee to get the right pre-commit-config.yaml installed
pre-commit install
```

## Building docs

```
source ~/.virtualenvs/domain-ee/bin/activate
cd ./domain-ee/max-cfi-app
python3 src/python_src/pull_api_documentation.py
```

## Docker Stuff

### Build the image

Follow steps for
[Platform Base + API-Gateway](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Docker-Compose#platform-base)
then run the max-cfi-api with the following command from the `abd_vro directory`:

```
./gradlew :domain-ee:dockerComposeUp
```

### Verify API in API Gateway

Navigate to [Swagger](http://localhost:8060/webjars/swagger-ui/index.html?urls.primaryName=3.%20Max%20CFI%20API)

Try to send a request on the post endpoint with the following request body:

```
{
  "diagnostic_codes": [
    6260
  ]
}
```
