import logging


def set_format():
    """
    Setup logger to automatically include date for messages sent to INFO

    """
    logger = logging.getLogger()
    ch = logging.StreamHandler()
    logger.setLevel(logging.INFO)

    formatter = logging.Formatter("%(asctime)s   %(levelname)s   %(message)s",
                                  "%Y-%m-%d %H:%M:%S")
    ch.setFormatter(formatter)
    ch.setLevel(logging.INFO)

    logger.addHandler(ch)

    return logger
