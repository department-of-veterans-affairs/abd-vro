# Getting started

### Install Python3.10
If you're on a Mac, you can use pyenv to handle multiple python versions

```
brew install pyenv
pyenv install python3.10
```

Set the global python so all further commands use installed version, or don't do this if you want a different version available globally for your system.
```
pyenv global python3.10
```

### Create a virtual env:
```
python -m venv ~/.virtualenvs/your-virtual-env # or wherever you want
source ~/.virtualenvs/your-virtual-env/bin/activate
```
Other tools such as [pyenv-virtualenv](https://github.com/pyenv/pyenv-virtualenv#installing-with-homebrew-for-macos-users) can be used to create and activate multiple virtual environments.

Make sure your python path is set up to pull from your virtualenv:
```
which python3
# /Users/<your_username>/.virtualenvs/your-virtual-env/bin/python
```

### Install dependencies
From your project folder, install dependencies.
```
pip install -r src/dev-requirements.txt
pip install -r src/requirements.txt
pip install -e .
```

## Unit, Integration, & End-to-End Tests

Make sure your virtual env is activated.

Navigate to the project folder and run the tests:

* Via pytest directly
    ```
    pytest .
    pytest ./integration
    pytest ./end_to_end
    ```
* Via gradle
    ```
    ./gradlew check
    ./gradlew integrationTest
    ./gradlew endToEndTest
    ```
