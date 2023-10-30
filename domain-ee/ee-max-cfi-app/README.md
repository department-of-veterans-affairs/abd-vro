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
            "max_rating": 10
        }
    ]
}
```

### Notes on usage:

#### Requests

* The `diagnostic_codes` array in the request are integers within the range of `5000 - 10000`.
    * Any request with an any entry that falls outside the range `5000 - 10000` will yield a `400`.
* An invalid request such as missing/invalid field will result in `422` status code.
* Duplicate entries in the `diagnostic_codes` array will yield a ratings array with unique entries.
* An empty `diagnostic_codes` array will yield an empty ratings array.
* A `diagnostic_codes` array with more than 1000 entries will yield a `422` status code.

#### Response

* The response contains a `ratings` array where each item contains a `diagnostic_code` and the associated `max_rating`.
    * The `diagnostic_code` corresponds to an entry in the requests `diagnostic_codes` array.
    * The `max_rating` item is a percentage expressed as an integer in the range of `0 - 100`.
* Each entry in `diagnostic_codes` array of the request with an associated max rating will yield an item in
  the `ratings` array of the response body.
* If any entry of the `diagnostic_codes` is not found, the response `ratings` array will not contain the corresponding
  item.

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
