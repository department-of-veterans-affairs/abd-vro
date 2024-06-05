package gov.va.vro.bip.service;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.RetryConfig;
import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Slf4j
public class MetricLoggerService {

  @Value("${env:dev}")
  private String ENV_VALUE;

  private static final String APP_PREFIX = "vro_bip";
  private static final String SERVICE_TAG = "service:vro-svc-bip-api";

  public enum METRIC {
    REQUEST,
    REQUEST_DURATION,
    RESPONSE_COMPLETE,
    RESPONSE_ERROR
  }

  private final MetricsApi metricsApi;

  public MetricLoggerService() {
    this(ApiClient.getDefaultApiClient());
    metricsApi.getApiClient().setRetry(new RetryConfig(true, 2, 2, 3));
  }

  public MetricLoggerService(ApiClient apiClient) {
    metricsApi = new MetricsApi(apiClient);
    log.info("initialized Datadog API client");
  }

  protected void setApiClient(ApiClient apiClient) {
    metricsApi.setApiClient(apiClient);
  }

  public static String getFullMetricString(@NotNull METRIC metric) {
    return String.format("%s.%s", APP_PREFIX, metric.name().toLowerCase());
  }

  public ArrayList<String> getTagsForSubmission(String[] customTags) {
    ArrayList<String> tags = new ArrayList<>();
    tags.add(String.format("environment:%s", ENV_VALUE));
    tags.add(SERVICE_TAG);
    if (customTags != null) {
      tags.addAll(Arrays.asList(customTags));
    }
    return tags;
  }

  public MetricsPayload createMetricsPayload(@NotNull METRIC metric, double value, String[] tags) {
    Series dataPointSeries =
        new Series(
            getFullMetricString(metric),
            Collections.singletonList(Collections.singletonList(value)));
    dataPointSeries.setType("count");

    dataPointSeries.setTags(getTagsForSubmission(tags));

    return new MetricsPayload().series(Collections.singletonList(dataPointSeries));
  }

  public void submitCount(@NotNull METRIC metric, String[] tags) {
    submitCount(metric, 1.0, tags);
  }

  public void submitCount(@NotNull METRIC metric, double value, String[] tags) {
    MetricsPayload payload = createMetricsPayload(metric, value, tags);

    try {
      IntakePayloadAccepted payloadResult = metricsApi.submitMetrics(payload);
      log.info(
          String.format(
              "submitted %s: %s",
              payload.getSeries().get(0).getMetric(), payloadResult.getStatus()));
    } catch (Exception e) {
      log.error(
          String.format(
              "exception submitting %s: %s",
              payload.getSeries().get(0).getMetric(), e.getMessage()));
    }
  }

  public DistributionPointsPayload createDistributionPointsPayload(
      @NotNull METRIC metric, double value, String[] tags) {
    DistributionPointItem distributionPointItem = new DistributionPointItem(value);
    DistributionPointsSeries dataPointSeries =
        new DistributionPointsSeries(
            getFullMetricString(metric),
            Collections.singletonList(Collections.singletonList(distributionPointItem)));
    dataPointSeries.setType(DistributionPointsType.DISTRIBUTION);
    dataPointSeries.setTags(getTagsForSubmission(tags));

    return new DistributionPointsPayload().series(Collections.singletonList(dataPointSeries));
  }

  public void submitDistribution(@NotNull METRIC metric, double value, String[] tags) {
    DistributionPointsPayload payload = createDistributionPointsPayload(metric, value, tags);
    try {
      IntakePayloadAccepted payloadResult = metricsApi.submitDistributionPoints(payload);
      log.info(
          String.format(
              "submitted %s: %s",
              payload.getSeries().get(0).getMetric(), payloadResult.getStatus()));
    } catch (Exception e) {
      log.error(
          String.format(
              "exception submitting %s: %s",
              payload.getSeries().get(0).getMetric(), e.getMessage()));
    }
  }
}
