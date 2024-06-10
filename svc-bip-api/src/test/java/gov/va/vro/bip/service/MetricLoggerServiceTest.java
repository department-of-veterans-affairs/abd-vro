package gov.va.vro.bip.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.datadog.api.client.v1.model.DistributionPointItem;
import com.datadog.api.client.v1.model.DistributionPointsPayload;
import com.datadog.api.client.v1.model.MetricsPayload;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class MetricLoggerServiceTest {

  private MetricLoggerService mls = new MetricLoggerService();

  @Test
  void testGetFullMetricString() {
    assertEquals(
        "vro_bip.request",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.REQUEST));
    assertEquals(
        "vro_bip.request_duration",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.REQUEST_DURATION));
    assertEquals(
        "vro_bip.response_complete",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.RESPONSE_COMPLETE));
    assertEquals(
        "vro_bip.response_error",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.RESPONSE_ERROR));
  }

  @Test
  void getTagsForSubmission() {
    List<String> tags =
        mls.getTagsForSubmission(new String[] {"source:integration-test", "version:2.1"});
    assertTrue(tags.contains("source:integration-test"));
    assertTrue(tags.contains("version:2.1"));
    assertTrue(tags.contains("service:vro-svc-bip-api"));
    assertTrue(tags.contains(String.format("environment:%s", System.getenv("env"))));
    assertEquals(tags.size(), 4);
  }

  @Test
  void getTagsForSubmissionNoCustomTags() {
    List<String> tags = mls.getTagsForSubmission(null);
    assertTrue(tags.contains("service:vro-svc-bip-api"));
    assertTrue(tags.contains(String.format("environment:%s", System.getenv("env"))));
    assertEquals(2, tags.size());
  }

  @Test
  void testCreateMetricsPayload() {
    MetricsPayload mp =
        mls.createMetricsPayload(
            MetricLoggerService.METRIC.RESPONSE_COMPLETE, 14.0, new String[] {"zone:purple"});
    assertEquals("count", mp.getSeries().get(0).getType());
    assertEquals(1, mp.getSeries().size());
    assertEquals("vro_bip.response_complete", mp.getSeries().get(0).getMetric());
    assertEquals(14.0, mp.getSeries().get(0).getPoints().get(0).get(1));
    assertTrue(Objects.requireNonNull(mp.getSeries().get(0).getTags()).contains("zone:purple"));
  }

  @Test
  void testCreateDistributionPointsPayload() {
    double timestamp =
        Long.valueOf(OffsetDateTime.now().toInstant().getEpochSecond()).doubleValue();
    DistributionPointsPayload dpl =
        mls.createDistributionPointsPayload(
            MetricLoggerService.METRIC.REQUEST_DURATION,
            timestamp,
            1523,
            new String[] {"food:pizza"});
    assertEquals(1, dpl.getSeries().size());
    assertEquals("vro_bip.request_duration", dpl.getSeries().get(0).getMetric());
    assertEquals(
        new DistributionPointItem(timestamp), dpl.getSeries().get(0).getPoints().get(0).get(0));
    assertEquals(
        new DistributionPointItem(List.of(1523.0)),
        dpl.getSeries().get(0).getPoints().get(0).get(1));
    assertTrue(Objects.requireNonNull(dpl.getSeries().get(0).getTags()).contains("food:pizza"));
    mls.submitCount(
        MetricLoggerService.METRIC.RESPONSE_COMPLETE,
        new String[] {"isTest:true", "source:ci-test"});
  }
}
