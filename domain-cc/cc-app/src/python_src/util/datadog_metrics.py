import logging
import os
from datetime import datetime

from datadog_api_client import ApiClient, Configuration
from datadog_api_client.exceptions import ApiException
from datadog_api_client.v1.api.metrics_api import MetricsApi
from datadog_api_client.v1.model.distribution_point import DistributionPoint
from datadog_api_client.v1.model.distribution_points_payload import (
    DistributionPointsPayload,
)
from datadog_api_client.v1.model.distribution_points_series import (
    DistributionPointsSeries,
)

configuration = Configuration(enable_retry=True)
api_client = ApiClient(configuration)
v1_metrics_api = MetricsApi(api_client)

APP_PREFIX = "cc-app"
ENV = os.environ.get("ENV") or "local"


def submit_duration_metric(metric: str, value: float):
    """
    Submits a duration metric with the name '{APP_PREFIX}.{metric}'
    :param metric: string containing the metric name
    :param value: value to submit
    """
    full_metric = f'{APP_PREFIX}.{metric.strip(".").lower()}'

    body = DistributionPointsPayload(
        series=[
            DistributionPointsSeries(
                metric=full_metric,
                points=[
                    DistributionPoint(
                        [
                            datetime.now().timestamp(),
                            [value],
                        ]
                    ),
                ],
                tags=[
                    f"environment:{ENV}",
                    f"service:{APP_PREFIX}",
                ],
            )
        ],
    )

    try:
        v1_metrics_api.submit_distribution_points(body=body)
    except ApiException as e:
        logging.warning(
            f"event=logMetricFailed metric={full_metric} type=duration value={value} status={e.status} reason={e.reason} body={e.body}"
        )
    except Exception as e:
        logging.warning(
            f'event=logMetricFailed metric={full_metric} type=duration value={value} type={type(e)} error="{e}"'
        )
