import logging
import yaml
from typing import Dict

stream = open('features.yml', 'r')
data = yaml.load(stream)

features = []

for k, v in data['features'].items():
  temp = [k, data['features'][k]['enabled']]
  features.append(temp)


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
            "features": features
        })

    logging.info("Message processed successfully")

    return response_body
