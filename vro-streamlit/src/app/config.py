from distutils.util import strtobool
from os import getenv

ENV = getenv('ENV', 'local')
DEBUG = bool(strtobool(getenv('DEBUG', 'False')))
