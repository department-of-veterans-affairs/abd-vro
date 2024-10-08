import logging
import sys

from app.config import DEBUG

level = logging.DEBUG if DEBUG else logging.INFO

logging.basicConfig(format='[%(asctime)s] %(levelname)-8s %(message)s', level=level, datefmt='%Y-%m-%d %H:%M:%S', stream=sys.stdout)
logger = logging.getLogger()
logger.debug('LOGGING DEBUG')
