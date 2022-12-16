import logging
from typing import Dict

from . import utils


def report_feature_toggles():
    """
    Take a request and return a response that includes feature toggle data

    :return: response body
    :rtype: dict
    """

    response_body = {}

    response_body.update(
        {
            "features": utils.create_features_list()
        })

    logging.info("Message processed successfully")

    return response_body
