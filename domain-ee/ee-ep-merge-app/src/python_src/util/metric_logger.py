import logging
from datetime import datetime

import datadog_api_client.v1.api.metrics_api as metrics_v1
import datadog_api_client.v2.api.metrics_api as metrics_v2
from config import ENV
from datadog_api_client import ApiClient, Configuration
from datadog_api_client.exceptions import ApiException
from datadog_api_client.v1.model.distribution_point import DistributionPoint
from datadog_api_client.v1.model.distribution_points_payload import (
    DistributionPointsPayload,
)
from datadog_api_client.v1.model.distribution_points_series import (
    DistributionPointsSeries,
)
from datadog_api_client.v2.model.metric_intake_type import MetricIntakeType
from datadog_api_client.v2.model.metric_payload import MetricPayload
from datadog_api_client.v2.model.metric_point import MetricPoint
from datadog_api_client.v2.model.metric_series import MetricSeries

APP_PREFIX = 'ep_merge'

ENV_TAG = f'environment:{ENV}'
SERVICE_TAG = 'service:vro-ee-ep-merge-app'
STANDARD_TAGS = [ENV_TAG, SERVICE_TAG]


configuration = Configuration(
    enable_retry=True
)
api_client = ApiClient(configuration)
count_metrics_api = metrics_v2.MetricsApi(api_client)
distribution_metrics_api = metrics_v1.MetricsApi(api_client)  # Metrics API does not have an endpoint for distribution metrics


def increment(metric: str, value: float = 1):
    """
    Increments a count metric with by the name 'APP_PREFIX.{metric}'
    :param metric: string containing the metric name
    :param value: value to increment by
    """

    body = MetricPayload(
        series=[
            MetricSeries(
                metric=f'{APP_PREFIX}.{metric.strip(".").lower()}',
                type=MetricIntakeType.COUNT,
                points=[
                    MetricPoint(
                        timestamp=int(datetime.now().timestamp()),
                        value=value,
                    ),
                ],
                tags=STANDARD_TAGS
            )
        ],
    )

    try:
        count_metrics_api.submit_metrics(body=body)
    except ApiException as e:
        logging.warning(f'event=logMetricFailed status={e.status} reason={e.reason} body={e.body}')
    except Exception as e:
        logging.warning(f'event=logMetricFailed type={type(e)} error="{e}"')


def distribution(metric: str, value: float):
    """
    Adds value to a distribution metric with by the name '{APP_PREFIX}.{metric}'
    :param metric: string containing the metric name
    :param value: value to increment by
    """

    body = DistributionPointsPayload(
        series=[
            DistributionPointsSeries(
                metric=f'{APP_PREFIX}.{metric.strip(".").lower()}.distribution',
                points=[
                    DistributionPoint(
                        [
                            datetime.now().timestamp(),
                            [value],
                        ]
                    ),
                ],
                tags=STANDARD_TAGS
            ),
        ],
    )

    try:
        distribution_metrics_api.submit_distribution_points(body=body)
    except ApiException as e:
        logging.warning(f'event=logMetricFailed status={e.status} reason={e.reason} body={e.body}')
    except Exception as e:
        logging.warning(f'event=logMetricFailed type={type(e)} error="{e}"')
