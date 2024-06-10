package gov.va.vro.bip.service;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.RetryConfig;
import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.*;
import gov.va.vro.bip.config.DatadogConfigProperties;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@ConfigurationProperties(prefix = "bip")
public class MetricLoggerService {

  public String env;

  private static final String APP_PREFIX = "vro_bip";
  private static final String SERVICE_TAG = "service:vro-svc-bip-api";

  public enum METRIC {
    REQUEST,
    REQUEST_DURATION,
    RESPONSE_COMPLETE,
    RESPONSE_ERROR,
    LISTENER_ERROR,
    MESSAGE_CONVERSION_ERROR
  }

  private MetricsApi metricsApi;

  public MetricLoggerService() {
    try {
      ApiClient apiClient = new ApiClient();
      HashMap<String, String> serverVariables = new HashMap<String, String>();
      serverVariables.put("site", DatadogConfigProperties.site);
      apiClient.setServerVariables(serverVariables);

      HashMap<String, String> secrets = new HashMap<String, String>();
      if (DatadogConfigProperties.api_key != null) {
        secrets.put("apiKeyAuth", DatadogConfigProperties.api_key);
      } else {
        log.warn("datadog api key not set");
      }
      if (DatadogConfigProperties.app_key != null) {
        secrets.put("appKeyAuth", DatadogConfigProperties.app_key);
      }
      apiClient.configureApiKeys(secrets);
      apiClient.setRetry(new RetryConfig(true, 2, 2, 3));

      metricsApi = new MetricsApi(ApiClient.getDefaultApiClient());
      log.info("initialized Datadog API client");
    } catch (Exception e) {
      log.error(String.format("exception initializing Datadog API client: %s", e.getMessage()));
    }
  }

  public static String getFullMetricString(@NotNull METRIC metric) {
    return String.format("%s.%s", APP_PREFIX, metric.name().toLowerCase());
  }

  public ArrayList<String> getTagsForSubmission(String[] customTags) {
    ArrayList<String> tags = new ArrayList<>();
    tags.add(String.format("environment:%s", env));
    tags.add(SERVICE_TAG);
    if (customTags != null) {
      tags.addAll(Arrays.asList(customTags));
    }
    return tags;
  }

  private static double getTimestamp() {
    return Long.valueOf(OffsetDateTime.now().toInstant().getEpochSecond()).doubleValue();
  }

  public MetricsPayload createMetricsPayload(@NotNull METRIC metric, double value, String[] tags) {
    Series dataPointSeries = new Series();
    dataPointSeries.setMetric(getFullMetricString(metric));
    dataPointSeries.setType("count");
    dataPointSeries.setTags(getTagsForSubmission(tags));
    dataPointSeries.setPoints(Collections.singletonList(Arrays.asList(getTimestamp(), value)));

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
      @NotNull METRIC metric, double timestamp, double value, String[] tags) {

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

  public void submitDistribution(@NotNull METRIC metric, double value, String[] tags) {
    submitDistribution(metric, getTimestamp(), value, tags);
  }

  public void submitDistribution(
      @NotNull METRIC metric, double timestamp, double value, String[] tags) {
    DistributionPointsPayload payload =
        createDistributionPointsPayload(metric, timestamp, value, tags);

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
