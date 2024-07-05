# Contention Classification
`/contention-classification/va-gov-claim-classifier` maps contention text and diagnostic codes from 526 submission to classifications as defined in the [Benefits Reference Data API](https://developer.va.gov/explore/benefits/docs/benefits_reference_data).

## Getting started
Codebase is part of the VRO gradle project and is automatically spun up when you run the gradle project locally.
See [Local Setup](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Local-Setup) on the wiki for gradle setup.
However, for a more speedy, lightweight setup, you can run the FastAPI server in a standalone terminal.

## FastAPI Setup
Install Python3.10
If you're on a mac, you can use pyenv to handle multiple python versions
```
brew install pyenv
pyenv install python3.10
pyenv global python3.10 # or don't do this if you want a different version available globally for your system
```

Create a virtual env
```
python -m venv ~/.virtualenvs/domain-cc # or wherever you want
source ~/.virtualenvs/domain-cc/bin/activate
```

Make sure your pythonpath is set up to pull from your vitualenv
```
which python3
# /Users/<your_username>/.virtualenvs/domain-cc/bin/python
```

Navigate to `src` directory, install dependencies, and run webserver
```
cd cc-app/src
pip3 install -r requirements.txt
uvicorn python_src.api:app --port 8120 --reload
```


Now you should be able to make a post request to the java code prefixed w/ "domain-cc" and the response from FastAPI will get sent back up

## Unit tests
Pytest is used for Python Unit tests. Make sure you're in your virtualenv
```
source ~/.virtualenvs/domain-cc/bin/activate
```
Navigate to cc-app/
```
cd domain-cc/cc-app
```
Run the tests
```
pytest
```


## Contributing
### Install dev dependencies
```
source ~/.virtualenvs/domain-cc/bin/activate
pip install -r dev-requirements.txt
# MAKE SURE you are in adb-vro/domain-cc to get the right pre-commit-config.yaml installed
pre-commit install
```

## Building docs
API Documentation is automatically created by FastAPI. This can be viewed at the /docs (e.g. localhost:8080/docs)
For exporting the open API spec and using documentation elsewhere, see [pull_api_documentation.py](https://github.com/department-of-veterans-affairs/abd-vro/blob/79bad1e34c98bada6dcfebe216820e52f4666df7/domain-cc/cc-app/src/python_src/util/pull_api_documentation.py)

## Docker Stuff
### Build the image
```
./gradlew :domain-cc:dockerComposeUp
```

### Misc
- if you're running into issues w/ the gradle project, try completely tearing everything down and deleting artifacts
```commandline
./gradlew :domain-cc:dockerComposeDown
./gradlew :app:dockerComposeDown
./gradlew :dockerComposeDown
./gradlew dcPrune
./gradlew dockerPruneVolume
./gradlew :dockerComposeUp
./gradlew :app:dockerComposeUp
./gradlew :domain-cc:dockerComposeUp
```
