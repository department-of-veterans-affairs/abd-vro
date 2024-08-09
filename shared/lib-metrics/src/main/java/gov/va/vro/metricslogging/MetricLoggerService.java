package gov.va.vro.metricslogging;

import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Conditional(NonLocalEnvironmentCondition.class)
public class MetricLoggerService implements IMetricLoggerService {

  private static final String APP_PREFIX = "vro_bip";
  private static final String SERVICE_TAG = "service:vro-svc-bip-api";

  private final MetricsApi metricsApi;

  public static double getTimestamp() {
    return Long.valueOf(OffsetDateTime.now().toInstant().getEpochSecond()).doubleValue();
  }

  public static double getElapsedTimeInMilliseconds(long startTimeNano, long endTimeNano) {
    // return as milliseconds the time between the start and end timestamps, where
    // the start and end timestamps are expressed in nanoseconds
    return (endTimeNano - startTimeNano) / 1000000.0;
  }

  public static String getFullMetricString(@NotNull METRIC metric) {
    // the name of the metric as queryable from datadog
    return String.format("%s.%s", APP_PREFIX, metric.name().toLowerCase());
  }

  @Override
  public ArrayList<String> getTagsForSubmission(String[] customTags) {
    // tags that will accompany the submitted data point(s).
    // a "key:value" format, while not required, can be convenient with querying metrics in the
    // datadog dashboard
    ArrayList<String> tags = new ArrayList<>();
    tags.add(SERVICE_TAG);
    if (customTags != null) {
      tags.addAll(Arrays.asList(customTags));
    }
    return tags;
  }

  @Override
  public MetricsPayload createMetricsPayload(@NotNull METRIC metric, double value, String[] tags) {
    //  create the payload for a count metric
    Series dataPointSeries = new Series();
    dataPointSeries.setMetric(getFullMetricString(metric));
    dataPointSeries.setType("count");
    dataPointSeries.setTags(getTagsForSubmission(tags));
    dataPointSeries.setPoints(Collections.singletonList(Arrays.asList(getTimestamp(), value)));

    return new MetricsPayload().series(Collections.singletonList(dataPointSeries));
  }

  @Override
  public void submitCount(@NotNull METRIC metric, String[] tags) {
    submitCount(metric, 1.0, tags);
  }

  @Override
  public void submitCount(@NotNull METRIC metric, double value, String[] tags) {
    MetricsPayload payload = createMetricsPayload(metric, value, tags);

    try {
      IntakePayloadAccepted payloadResult = metricsApi.submitMetrics(payload);
      log.info(
          String.format(
              "submitted %s: %s",
              payload.getSeries().get(0).getMetric(), payloadResult.getStatus()));
    } catch (Exception e) {
      log.warn(
          String.format(
              "exception submitting %s: %s",
              payload.getSeries().get(0).getMetric(), e.getMessage()));
    }
  }

  @Override
  public DistributionPointsPayload createDistributionPointsPayload(
      @NotNull METRIC metric, double timestamp, double value, String[] tags) {
    //  create the payload for a distribution metric

    DistributionPointsSeries dataPointSeries = new DistributionPointsSeries();
    dataPointSeries.setMetric(getFullMetricString(metric));
    dataPointSeries.setPoints(
        List.of(
            Arrays.asList(
                new DistributionPointItem(getTimestamp()),
                new DistributionPointItem(List.of(value)))));
    dataPointSeries.setType(DistributionPointsType.DISTRIBUTION);
    dataPointSeries.setTags(getTagsForSubmission(tags));

    return new DistributionPointsPayload().series(Collections.singletonList(dataPointSeries));
  }

  @Override
  public void submitRequestDuration(
      long requestStartNanoseconds, long requestEndNanoseconds, String[] tags) {

    DistributionPointsPayload payload =
        createDistributionPointsPayload(
            METRIC.REQUEST_DURATION,
            getTimestamp(),
            getElapsedTimeInMilliseconds(requestStartNanoseconds, requestEndNanoseconds),
            tags);

    try {
      IntakePayloadAccepted payloadResult = metricsApi.submitDistributionPoints(payload);
      log.info(
          String.format(
              "submitted %s: %s",
              payload.getSeries().get(0).getMetric(), payloadResult.getStatus()));
    } catch (Exception e) {
      log.warn(
          String.format(
              "exception submitting %s: %s",
              payload.getSeries().get(0).getMetric(), e.getMessage()));
    }
  }
}
