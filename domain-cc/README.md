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

Install dependencies and run webserver
```
pip3 install -r requirements.txt
uvicorn api:app --reload
```

## Contributing
### Install dev dependencies
```
source ~/.virtualenvs/domain-cc/bin/activate
pip install dev-requirements.txt
# MAKE SURE you are in adb-vro/domain-cc to get the right pre-commit-config.yaml installed
pre-commit install
```
