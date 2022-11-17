import logging
from typing import Dict

import yaml

from . import utils


def report_feature_toggles(event: Dict):
    """
    Take a request and return a response that includes feature toggle data

    :param event: request body
    :type event: dict
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
