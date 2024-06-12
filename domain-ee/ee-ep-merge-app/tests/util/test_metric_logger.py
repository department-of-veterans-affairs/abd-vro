import pytest
from datadog_api_client.v2.model.metric_intake_type import MetricIntakeType

from src.python_src.util.metric_logger import (
    CountMetric,
    DistributionMetric,
    distribution,
    increment,
)


@pytest.fixture()
def count_metrics_api(mocker):
    return mocker.patch('src.python_src.util.metric_logger.count_metrics_api')


@pytest.fixture()
def distribution_metrics_api(mocker):
    return mocker.patch('src.python_src.util.metric_logger.distribution_metrics_api')


@pytest.mark.asyncio()
async def test_increment_with_one_metric_to_increment(count_metrics_api):
    metrics = [CountMetric(name='test_metric', value=1)]

    await increment(metrics)

    body = count_metrics_api.submit_metrics.call_args[1]['body']

    assert len(body.series) == 1
    assert body.series[0].metric == 'ep_merge.test_metric'
    assert body.series[0].type == MetricIntakeType.COUNT
    assert len(body.series[0].points) == 1
    assert body.series[0].points[0].value == 1


@pytest.mark.asyncio()
async def test_increment_with_multiple_metrics_to_increment(count_metrics_api):
    metrics = [CountMetric(name='test_metric_1', value=1), CountMetric(name='test_metric_2', value=2)]

    await increment(metrics)

    body = count_metrics_api.submit_metrics.call_args[1]['body']

    assert len(body.series) == 2
    assert all(s.type == MetricIntakeType.COUNT for s in body.series)

    assert body.series[0].metric == 'ep_merge.test_metric_1'
    assert len(body.series[0].points) == 1
    assert body.series[0].points[0].value == 1

    assert body.series[1].metric == 'ep_merge.test_metric_2'
    assert len(body.series[1].points) == 1
    assert body.series[1].points[0].value == 2


@pytest.mark.asyncio()
async def test_increment_with_one_metric_to_distribution(distribution_metrics_api):
    metrics = [DistributionMetric(name='test_metric', value=1.5)]

    await distribution(metrics)

    body = distribution_metrics_api.submit_distribution_points.call_args[1]['body']

    assert len(body.series) == 1
    assert body.series[0].metric == 'ep_merge.test_metric.distribution'
    assert len(body.series[0].points) == 1
    points = body.series[0].points[0].value[1]
    assert len(points) == 1
    assert points[0] == 1.5


@pytest.mark.asyncio()
async def test_increment_with_multiple_metrics_to_distribution(distribution_metrics_api):
    metrics = [DistributionMetric(name='test_metric_1', value=1.1), DistributionMetric(name='test_metric_2', value=2.2)]

    await distribution(metrics)

    body = distribution_metrics_api.submit_distribution_points.call_args[1]['body']

    assert len(body.series) == 2

    assert body.series[0].metric == 'ep_merge.test_metric_1.distribution'
    assert len(body.series[0].points) == 1
    points = body.series[0].points[0].value[1]
    assert len(points) == 1
    assert points[0] == 1.1

    assert body.series[1].metric == 'ep_merge.test_metric_2.distribution'
    assert len(body.series[1].points) == 1
    points = body.series[1].points[0].value[1]
    assert len(points) == 1
    assert points[0] == 2.2
