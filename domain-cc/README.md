# Contention Classification
`/contention-classification/classifier` maps contention diagnostic codes to classifications as defined in the [Benefits Reference Data API](https://developer.va.gov/explore/benefits/docs/benefits_reference_data).

## Getting started
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

## testing it all together
Get the java project up and running
See [Local Setup](https://github.com/department-of-veterans-affairs/abd-vro/wiki/Local-Setup) on the wiki.

Run the Python webserver (uvicorn command above)

After you have the vro-app-1 container and FastAPI running...
In another terminal, run the RabbitMQ client
```
# source ~/.virtualenvs/domain-cc/bin/activate
python3 rabbitmq_client.py
```

Now you should be able to make a post request to the java code prefixed w/ "domain-cc" and the response from FastAPI will get sent back up

## Unit tests
Make sure you're in your virtualenv
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

## Database Migrations
This application uses alembic to perform any database migrations or updates.  To perform migrations follow the below steps:
```
# Run vro application from abd-vro directory
./gradlew :dockerComposeUp

# Navigate to cc-app directory
cd domain-cc/cc-app

# Apply database migrations
alembic upgrade head
```

If there are any updates needed for the database (new tables, remove tables, add fields, etc.) you can use alembic to generate the revision file and apply the migrations following the below steps.

1. Update the data models in `abd-vro/domain-cc/cc-app/alembic/db_models.py`
2. From `cc-app` directory run `alembic revision --autogenerate -m <add note about changes>`
3. Run `alembic upgrade head` to apply migrations

If migration commands are run without updating the data models, an empty migrations file will be created in the `alembic/versions` directory.  These migrations will only be applied to the domain-cc schema.

## Contributing
### Install dev dependencies
```
source ~/.virtualenvs/domain-cc/bin/activate
pip install -r dev-requirements.txt
# MAKE SURE you are in adb-vro/domain-cc to get the right pre-commit-config.yaml installed
pre-commit install
```

## Building docs
```
source ~/.virtualenvs/domain-cc/bin/activate
cd src
python util/pull_api_documentation.py
cp ./fastapi.json ../../app/src/main/java/gov/va/vro/config
# somehow make the java code pull that json in
```

## Docker Stuff
### Build the image
```
./gradlew :domain-cc:dockerComposeUp
```
