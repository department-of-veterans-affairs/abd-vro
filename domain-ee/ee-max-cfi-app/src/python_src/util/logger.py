import logging
import sys

logging.basicConfig(format='[%(asctime)s] %(levelname)-8s %(message)s', level=logging.INFO, datefmt='%Y-%m-%d %H:%M:%S', stream=sys.stdout)
logger = logging.getLogger()
