import logging
import time
from typing import NamedTuple

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

ENV_TAG = f'env:{ENV}'
SERVICE_TAG = 'service:vro-ee-ep-merge-app'
TEAM_TAG = 'team:benefits'
IT_PORTFOLIO_TAG = 'itportfolio:benefits-delivery'
DEPENDENCY_TAGS = ['dependency:svc-bip-api', 'dependency:svc-bgs-api']

STANDARD_TAGS = [ENV_TAG, SERVICE_TAG, TEAM_TAG, IT_PORTFOLIO_TAG]
STANDARD_TAGS.extend(DEPENDENCY_TAGS)


configuration = Configuration(enable_retry=True)
api_client = ApiClient(configuration)
count_metrics_api = metrics_v2.MetricsApi(api_client)
distribution_metrics_api = metrics_v1.MetricsApi(api_client)  # Metrics API does not have an endpoint for distribution metrics


class CountMetric(NamedTuple):
    name: str
    value: float = 1


class DistributionMetric(NamedTuple):
    name: str
    value: float = 1


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
                    timestamp=int(time.time()),
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
        logging.warning(f'event=logMetricFailed type=count metrics={metrics} status={e.status} reason={e.reason} body={e.body}')
    except Exception as e:
        logging.warning(f'event=logMetricFailed type=count metrics={metrics} error_type={type(e)} error="{e}"')


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
                        int(time.time()),
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
        logging.warning(f"event=logMetricFailed type=distribution metrics={metrics} error_type={type(e)} error='{e}'")
