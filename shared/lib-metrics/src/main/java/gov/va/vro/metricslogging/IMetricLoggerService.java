package gov.va.vro.metricslogging;

import com.datadog.api.client.v1.model.DistributionPointsPayload;
import com.datadog.api.client.v1.model.MetricsPayload;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;

public interface IMetricLoggerService {

  ArrayList<String> getTagsForSubmission(String[] customTags);

  MetricsPayload createMetricsPayload(@NotNull METRIC metric, double value, String[] tags);

  void submitCount(@NotNull METRIC metric, String[] tags);

  void submitCount(@NotNull METRIC metric, double value, String[] tags);

  DistributionPointsPayload createDistributionPointsPayload(
      @NotNull METRIC metric, double timestamp, double value, String[] tags);

  void submitRequestDuration(
      long requestStartNanoseconds, long requestEndNanoseconds, String[] tags);

  enum METRIC {
    REQUEST_START,
    REQUEST_DURATION,
    RESPONSE_COMPLETE,
    RESPONSE_ERROR,
    LISTENER_ERROR,
    MESSAGE_CONVERSION_ERROR
  }
}
