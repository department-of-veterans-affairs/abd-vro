import logging
from datetime import datetime

import datadog_api_client.v1.api.metrics_api as metrics_v1
import datadog_api_client.v2.api.metrics_api as metrics_v2
from config import ENV
from datadog_api_client import ApiClient, Configuration
from datadog_api_client.exceptions import ApiException
from datadog_api_client.v1.model.distribution_point import DistributionPoint
from datadog_api_client.v1.model.distribution_points_content_encoding import (
    DistributionPointsContentEncoding,
)
from datadog_api_client.v1.model.distribution_points_payload import (
    DistributionPointsPayload,
)
from datadog_api_client.v1.model.distribution_points_series import (
    DistributionPointsSeries,
)
from datadog_api_client.v2.model.metric_content_encoding import MetricContentEncoding
from datadog_api_client.v2.model.metric_intake_type import MetricIntakeType
from datadog_api_client.v2.model.metric_payload import MetricPayload
from datadog_api_client.v2.model.metric_point import MetricPoint
from datadog_api_client.v2.model.metric_series import MetricSeries

APP_PREFIX = 'ep_merge'

ENV_TAG = f'environment:{ENV}'
SERVICE_TAG = 'service:vro-ee-ep-merge-app'
STANDARD_TAGS = [ENV_TAG, SERVICE_TAG]


configuration = Configuration(enable_retry=True)
api_client = ApiClient(configuration)
count_metrics_api = metrics_v2.MetricsApi(api_client)
distribution_metrics_api = metrics_v1.MetricsApi(api_client)  # Metrics API does not have an endpoint for distribution metrics


class Metric:
    """
    A class used to represent a base Metric.

    Attributes:
        name (str): The name of the metric
        value (float): The value of the metric
    """

    def __init__(self, name: str, value: float):
        self.name = name
        self.value = value

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.name == other.name and self.value == other.value

    def __hash__(self):
        return hash((self.name, self.value))


class CountMetric(Metric):
    def __init__(self, name: str, value: float = 1):
        super().__init__(name, value)

    def __repr__(self):
        return f'CountMetric(name={self.name}, value={self.value})'


class DistributionMetric(Metric):
    def __repr__(self):
        return f'DistributionMetric(name={self.name}, value={self.value})'


def increment(metrics: list[CountMetric]) -> None:
    """
    Adds value to a count metric with by the name '{APP_PREFIX}.{metric.name.strip(".").lower()}'
    :param metrics: list of CountMetric objects
    """
    series = [
        MetricSeries(
            metric=f'{APP_PREFIX}.{metric.name.strip(".").lower()}',
            type=MetricIntakeType.COUNT,
            points=[
                MetricPoint(
                    timestamp=int(datetime.now().timestamp()),
                    value=metric.value,
                ),
            ],
            tags=STANDARD_TAGS,
        )
        for metric in metrics
    ]

    body = MetricPayload(series=series)

    try:
        count_metrics_api.submit_metrics(body=body, content_encoding=MetricContentEncoding.DEFLATE)
    except ApiException as e:
        logging.warning(f'event=logMetricFailed metrics={metrics} type=count status={e.status} reason={e.reason} body={e.body}')
    except Exception as e:
        logging.warning(f'event=logMetricFailed metrics={metrics} type=count type={type(e)} error="{e}"')


def distribution(metrics: list[DistributionMetric]) -> None:
    """
    Adds value to a distribution metric with by the name '{APP_PREFIX}.{metric.name.strip(".").lower()}.distribution'
    :param metrics: list of DistributionMetric objects
    """

    series = [
        DistributionPointsSeries(
            metric=f'{APP_PREFIX}.{metric.name.strip(".").lower()}.distribution',
            points=[
                DistributionPoint(
                    [
                        datetime.now().timestamp(),
                        [metric.value],
                    ]
                ),
            ],
            tags=STANDARD_TAGS,
        )
        for metric in metrics
    ]

    body = DistributionPointsPayload(series=series)

    try:
        distribution_metrics_api.submit_distribution_points(content_encoding=DistributionPointsContentEncoding.DEFLATE, body=body)
    except ApiException as e:
        logging.warning(f"event=logMetricFailed type=distribution metrics={metrics} status={e.status} reason={e.reason} body='{e.body}'")
    except Exception as e:
        logging.warning(f"event=logMetricFailed type=distribution metrics={metrics} type={type(e)} error='{e}'")
