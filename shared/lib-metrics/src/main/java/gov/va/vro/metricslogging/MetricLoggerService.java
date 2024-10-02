package gov.va.vro.metricslogging;

import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Conditional(NonLocalEnvironmentCondition.class)
public class MetricLoggerService implements IMetricLoggerService {

  private final MetricsApi metricsApi;

  @Value("${vro.env}")
  public final String env;

  @Value("${vro.it-portfolio:benefits-delivery}")
  public final String itPortfolio;

  @Value("${vro.team:va-abd-rrd}")
  public final String team;

  @Value("${vro.app.name}")
  public final String service;

  @Value("${vro.app.dependency:")
  public final String dependency;

  public static double getTimestamp() {
    return Long.valueOf(OffsetDateTime.now().toInstant().getEpochSecond()).doubleValue();
  }

  public static double getElapsedTimeInMilliseconds(long startTimeNano, long endTimeNano) {
    // return as milliseconds the time between the start and end timestamps, where
    // the start and end timestamps are expressed in nanoseconds
    return (endTimeNano - startTimeNano) / 1000000.0;
  }

  public static String getFullMetricString(@NotNull String metricPrefix, @NotNull METRIC metric) {
    // the name of the metric as queryable from datadog
    return String.format("%s.%s", metricPrefix, metric.name().toLowerCase());
  }

  @Override
  public ArrayList<String> getTagsForSubmission(String[] customTags) {
    // tags that will accompany the submitted data point(s).
    // a "key:value" format, while not required, can be convenient with querying metrics in the
    // datadog dashboard
    ArrayList<String> tags = new ArrayList<>();
    if (customTags != null) {
      tags.addAll(Arrays.asList(customTags));
    }

    tags.add("env:" + env);
    tags.add("itportfolio:" + itPortfolio);
    tags.add("team:" + team);
    tags.add("service:" + service);
    if (dependency != null && !dependency.isEmpty()) {
      tags.add("dependency:" + dependency);
    }

    return tags;
  }

  @Override
  public MetricsPayload createMetricsPayload(
      @NotNull String metricPrefix, @NotNull METRIC metric, double value, String[] tags) {
    //  create the payload for a count metric
    Series dataPointSeries = new Series();
    dataPointSeries.setMetric(getFullMetricString(metricPrefix, metric));
    dataPointSeries.setType("count");
    dataPointSeries.setTags(getTagsForSubmission(tags));
    dataPointSeries.setPoints(Collections.singletonList(Arrays.asList(getTimestamp(), value)));

    return new MetricsPayload().series(Collections.singletonList(dataPointSeries));
  }

  @Override
  public void submitCount(@NotNull String metricPrefix, @NotNull METRIC metric, String[] tags) {
    submitCount(metricPrefix, metric, 1.0, tags);
  }

  @Override
  public void submitCount(
      @NotNull String metricPrefix, @NotNull METRIC metric, double value, String[] tags) {
    MetricsPayload payload = createMetricsPayload(metricPrefix, metric, value, tags);

    try {
      metricsApi
          .submitMetricsAsync(payload)
          .whenComplete(
              (payloadAccepted, ex) -> {
                if (ex != null) {
                  log.warn(String.format("exception submitting %s: %s", metric, ex.getMessage()));
                } else {
                  log.info(String.format("submitted %s: %s", metric, payloadAccepted.getStatus()));
                }
              });
    } catch (Exception e) {
      log.warn(String.format("exception submitting %s: %s", metric, e.getMessage()));
    }
  }

  @Override
  public DistributionPointsPayload createDistributionPointsPayload(
      @NotNull String metricPrefix,
      @NotNull METRIC metric,
      double timestamp,
      double value,
      String[] tags) {
    //  create the payload for a distribution metric

    DistributionPointsSeries dataPointSeries = new DistributionPointsSeries();
    dataPointSeries.setMetric(getFullMetricString(metricPrefix, metric));
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
      @NotNull String metricPrefix,
      long requestStartNanoseconds,
      long requestEndNanoseconds,
      String[] tags) {

    DistributionPointsPayload payload =
        createDistributionPointsPayload(
            metricPrefix,
            METRIC.REQUEST_DURATION,
            getTimestamp(),
            getElapsedTimeInMilliseconds(requestStartNanoseconds, requestEndNanoseconds),
            tags);

    try {
      metricsApi
          .submitDistributionPointsAsync(payload)
          .whenComplete(
              (payloadAccepted, ex) -> {
                if (ex != null) {
                  log.warn(
                      String.format(
                          "exception submitting %s: %s", METRIC.REQUEST_DURATION, ex.getMessage()));
                } else {
                  log.info(
                      String.format(
                          "submitted %s: %s",
                          METRIC.REQUEST_DURATION, payloadAccepted.getStatus()));
                }
              });
    } catch (Exception e) {
      log.warn(
          String.format("exception submitting %s: %s", METRIC.REQUEST_DURATION, e.getMessage()));
    }
  }
}
