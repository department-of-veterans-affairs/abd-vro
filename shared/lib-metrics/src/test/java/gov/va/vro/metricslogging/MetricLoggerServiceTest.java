package gov.va.vro.metricslogging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.DistributionPointItem;
import com.datadog.api.client.v1.model.DistributionPointsPayload;
import com.datadog.api.client.v1.model.MetricsPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class MetricLoggerServiceTest {

  private MetricLoggerService mls = new MetricLoggerService(new MetricsApi());

  @Test
  void testConstructors() {
    try {
      new MetricLoggerService(new MetricsApi());
    } catch (Exception e) {
      fail("Constructor failed", e);
    }
  }

  @Test
  void testGetFullMetricString() {
    assertEquals(
        "vro_bip.request_start",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.REQUEST_START));
    assertEquals(
        "vro_bip.request_duration",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.REQUEST_DURATION));
    assertEquals(
        "vro_bip.response_complete",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.RESPONSE_COMPLETE));
    assertEquals(
        "vro_bip.response_error",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.RESPONSE_ERROR));
    assertEquals(
        "vro_bip.listener_error",
        MetricLoggerService.getFullMetricString(MetricLoggerService.METRIC.LISTENER_ERROR));
    assertEquals(
        "vro_bip.message_conversion_error",
        MetricLoggerService.getFullMetricString(
            MetricLoggerService.METRIC.MESSAGE_CONVERSION_ERROR));
  }

  @Test
  void getTagsForSubmission() {
    List<String> tags =
        mls.getTagsForSubmission(new String[] {"source:integration-test", "version:2.1"});
    assertTrue(tags.contains("source:integration-test"));
    assertTrue(tags.contains("version:2.1"));
    assertTrue(tags.contains("service:vro-svc-bip-api"));
    assertEquals(tags.size(), 3);
  }

  @Test
  void getTagsForSubmissionNoCustomTags() {
    List<String> tags = mls.getTagsForSubmission(null);
    assertTrue(tags.contains("service:vro-svc-bip-api"));
    assertEquals(1, tags.size());
  }

  @Test
  void testCreateMetricsPayload() {
    MetricsPayload mp =
        mls.createMetricsPayload(
            MetricLoggerService.METRIC.RESPONSE_COMPLETE, 14.0, new String[] {"zone:purple"});
    Assertions.assertEquals("count", mp.getSeries().get(0).getType());
    Assertions.assertEquals(1, mp.getSeries().size());
    Assertions.assertEquals("vro_bip.response_complete", mp.getSeries().get(0).getMetric());
    Assertions.assertEquals(14.0, mp.getSeries().get(0).getPoints().get(0).get(1));
    Assertions.assertTrue(
        Objects.requireNonNull(mp.getSeries().get(0).getTags()).contains("zone:purple"));
  }

  @Test
  void testGetTimestamp() {
    // verify that the timestamp is current

    double timestamp =
        Long.valueOf(OffsetDateTime.now().toInstant().getEpochSecond()).doubleValue();

    double timestampToTest = MetricLoggerService.getTimestamp();

    assertTrue((timestamp - 10) < timestampToTest);
    assertTrue(timestampToTest < (timestamp + 10));
  }

  @Test
  void testCreateDistributionPointsPayload() {
    double timestamp = MetricLoggerService.getTimestamp();

    DistributionPointsPayload dpl =
        mls.createDistributionPointsPayload(
            MetricLoggerService.METRIC.REQUEST_DURATION,
            timestamp,
            1523,
            new String[] {"food:pizza"});
    Assertions.assertEquals(1, dpl.getSeries().size());
    Assertions.assertEquals("vro_bip.request_duration", dpl.getSeries().get(0).getMetric());
    Assertions.assertEquals(
        new DistributionPointItem(timestamp), dpl.getSeries().get(0).getPoints().get(0).get(0));
    Assertions.assertEquals(
        new DistributionPointItem(List.of(1523.0)),
        dpl.getSeries().get(0).getPoints().get(0).get(1));
    Assertions.assertTrue(
        Objects.requireNonNull(dpl.getSeries().get(0).getTags()).contains("food:pizza"));
  }

  @Test
  void testSubmitCountCallsApiWithPayload() {
    MetricsApi metricsApi = mock(MetricsApi.class);
    MetricLoggerService mls = new MetricLoggerService(metricsApi);
    mls.submitCount(IMetricLoggerService.METRIC.RESPONSE_COMPLETE, null);
    mls.submitCount(IMetricLoggerService.METRIC.RESPONSE_COMPLETE, 3.0, null);
    try {
      verify(metricsApi, times(2)).submitMetrics(ArgumentMatchers.any(MetricsPayload.class));
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void testSubmitRequestDurationCallsApiWithPayload() {
    MetricsApi metricsApi = mock(MetricsApi.class);
    MetricLoggerService mls = new MetricLoggerService(metricsApi);
    mls.submitRequestDuration(100, 200, null);
    try {
      verify(metricsApi, times(1))
          .submitDistributionPoints(ArgumentMatchers.any(DistributionPointsPayload.class));
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  public void testGetElapsedTimeInMilliseconds() {
    long nowInNano = System.nanoTime();
    double timeDelayInNano = 721000; // equivalent to .721 ms
    double elapsedTimeInMs =
        MetricLoggerService.getElapsedTimeInMilliseconds(
            nowInNano, (long) (nowInNano + timeDelayInNano));
    assertEquals(0.721, elapsedTimeInMs);
  }
}
