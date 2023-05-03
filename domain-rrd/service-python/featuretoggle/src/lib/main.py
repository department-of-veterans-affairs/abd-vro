import logging

from . import utils


def report_feature_toggles():
    """
    Take a request and return a response that includes feature toggle data

    :return: response body
    :rtype: dict
    """

    response_body = {
        "features": utils.create_features_list()
    }

    logging.info("Message processed successfully")

    return response_body
