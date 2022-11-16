import logging
# import yaml
from typing import Dict

# stream = open('features.yml', 'r')
# data = yaml.load(stream)
#
# for item in d['features'].items()
# v['enabled'] for v in d['features'].values()

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
            "features": {}
        })

    logging.info("Message processed successfully")

    return response_body
