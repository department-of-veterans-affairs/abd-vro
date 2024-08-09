package gov.va.vro.metricslogging;

import com.datadog.api.client.v1.model.DistributionPointsPayload;
import com.datadog.api.client.v1.model.MetricsPayload;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Conditional(LocalEnvironmentCondition.class)
public class NoopMetricLoggerService implements IMetricLoggerService {

  @Override
  public ArrayList<String> getTagsForSubmission(String[] customTags) {
    return new ArrayList<>();
  }

  @Override
  public MetricsPayload createMetricsPayload(METRIC metric, double value, String[] tags) {
    return new MetricsPayload();
  }

  @Override
  public void submitCount(METRIC metric, String[] tags) {}

  @Override
  public void submitCount(METRIC metric, double value, String[] tags) {}

  @Override
  public DistributionPointsPayload createDistributionPointsPayload(
      METRIC metric, double timestamp, double value, String[] tags) {
    return new DistributionPointsPayload();
  }

  @Override
  public void submitRequestDuration(
      long requestStartNanoseconds, long requestEndNanoseconds, String[] tags) {}
}
