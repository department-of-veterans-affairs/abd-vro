import logging

import datadog
from config import ENV

APP_PREFIX = 'ep_merge'

ENV_TAG = f'environment:{ENV}'
SERVICE_TAG = 'service:vro-ee-ep-merge-app'
STANDARD_TAGS = [ENV_TAG, SERVICE_TAG]


def start():
    logging.info('Datadog DogStatsD initializing...')
    datadog.initialize()
    logging.info('Datadog DogStatsD initialized.')


def stop():
    logging.info('Datadog DogStatsD flushing...')
    datadog.statsd.flush()
    logging.info('Datadog DogStatsD flushed. Closing socket...')
    datadog.statsd.close_socket()
    logging.info('Datadog DogStatsD socket closed.')


def increment(metric: str, value: float = 1, tags: list = None):
    """
    Increments a count metric with by the name 'APP_PREFIX.{metric}'
    :param metric: string containing the metric name
    :param value: value to increment by
    :param tags: any associated tags with the metric
    """

    if not tags:
        tags = STANDARD_TAGS
    else:
        tags.extend(STANDARD_TAGS)

    datadog.statsd.increment(f'{APP_PREFIX}.{metric.strip(".").lower()}', value, tags)


def distribution(metric: str, value: float = 1, tags: list = None):
    """
    Adds value to a distribution metric with by the name '{APP_PREFIX}.{metric}'
    :param metric: string containing the metric name
    :param value: value to increment by
    :param tags: any associated tags with the metric
    """

    if not tags:
        tags = STANDARD_TAGS
    else:
        tags.extend(STANDARD_TAGS)

    datadog.statsd.distribution(f'{APP_PREFIX}.{metric.strip(".").lower()}.distribution', value, tags)
