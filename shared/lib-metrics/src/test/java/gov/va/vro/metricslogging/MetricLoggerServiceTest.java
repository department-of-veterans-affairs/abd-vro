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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MetricLoggerServiceTest {

  private MetricLoggerService mls =
      new MetricLoggerService(
          new MetricsApi(),
          "env",
          "benefits-delivery",
          "va-abd-rrd",
          "the-service",
          Set.of("dep_1", "dep_2"),
          METRICS_PREFIX);
  private static final String METRICS_PREFIX = "vro_short_app_name";

  @Test
  void testConstructors() {
    try {
      new MetricLoggerService(
          new MetricsApi(), "", "", "", "", Collections.emptySet(), METRICS_PREFIX);
    } catch (Exception e) {
      fail("Constructor failed", e);
    }
  }

  @Test
  void testGetFullMetricString() {
    assertEquals(
        String.format("%s.request_start", METRICS_PREFIX),
        MetricLoggerService.getFullMetricString(
            METRICS_PREFIX, MetricLoggerService.METRIC.REQUEST_START));
    assertEquals(
        String.format("%s.request_duration", METRICS_PREFIX),
        MetricLoggerService.getFullMetricString(
            METRICS_PREFIX, MetricLoggerService.METRIC.REQUEST_DURATION));
    assertEquals(
        String.format("%s.response_complete", METRICS_PREFIX),
        MetricLoggerService.getFullMetricString(
            METRICS_PREFIX, MetricLoggerService.METRIC.RESPONSE_COMPLETE));
    assertEquals(
        String.format("%s.response_error", METRICS_PREFIX),
        MetricLoggerService.getFullMetricString(
            METRICS_PREFIX, MetricLoggerService.METRIC.RESPONSE_ERROR));
    assertEquals(
        String.format("%s.listener_error", METRICS_PREFIX),
        MetricLoggerService.getFullMetricString(
            METRICS_PREFIX, MetricLoggerService.METRIC.LISTENER_ERROR));
    assertEquals(
        String.format("%s.message_conversion_error", METRICS_PREFIX),
        MetricLoggerService.getFullMetricString(
            METRICS_PREFIX, MetricLoggerService.METRIC.MESSAGE_CONVERSION_ERROR));
  }

  @Test
  void getTagsForSubmission() {
    List<String> tags =
        mls.getTagsForSubmission(new String[] {"source:integration-test", "version:2.1"});
    assertTrue(tags.contains("source:integration-test"));
    assertTrue(tags.contains("version:2.1"));

    // two tags above; 4 required tags + 2 dependency tags
    assertEquals(tags.size(), 8);

    List<String> requiredTags = List.of("env:", "team:", "itportfolio:", "service:", "dependency:");
    for (String requiredTag : requiredTags) {
      if (tags.stream().noneMatch(t -> t.startsWith(requiredTag))) {
        fail("Required tag " + requiredTag + " not found in tags: " + tags);
      }
    }
  }

  @Test
  void getTagsForSubmissionNoCustomTags() {
    List<String> tags = mls.getTagsForSubmission(null);

    // 4 required tags + 2 dependency tags
    assertEquals(tags.size(), 6);

    // check that all required tags are present
    List<String> requiredTags = List.of("env:", "team:", "itportfolio:", "service:", "dependency:");
    for (String requiredTag : requiredTags) {
      if (tags.stream().noneMatch(t -> t.startsWith(requiredTag))) {
        fail("Required tag " + requiredTag + " not found in tags: " + tags);
      }
    }

    // check that no extra tags were added since we passed null above
    for (String tag : tags) {
      if (requiredTags.stream().noneMatch(t -> t.startsWith(tag.substring(0, t.indexOf(":"))))) {
        fail("Unexpected tag: " + tag);
      }
    }
  }

  @Test
  void testCreateMetricsPayload() {
    MetricsPayload mp =
        mls.createMetricsPayload(
            MetricLoggerService.METRIC.RESPONSE_COMPLETE, 14.0, new String[] {"zone:purple"});
    Assertions.assertEquals("count", mp.getSeries().get(0).getType());
    Assertions.assertEquals(1, mp.getSeries().size());
    Assertions.assertEquals(
        String.format("%s.response_complete", METRICS_PREFIX), mp.getSeries().get(0).getMetric());
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
    Assertions.assertEquals(
        String.format("%s.request_duration", METRICS_PREFIX), dpl.getSeries().get(0).getMetric());
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
    MetricLoggerService mls =
        new MetricLoggerService(metricsApi, "", "", "", "", Collections.emptySet(), METRICS_PREFIX);
    mls.submitCount(IMetricLoggerService.METRIC.RESPONSE_COMPLETE, null);
    mls.submitCount(IMetricLoggerService.METRIC.RESPONSE_COMPLETE, 3.0, null);
    try {
      verify(metricsApi, times(2)).submitMetricsAsync(ArgumentMatchers.any(MetricsPayload.class));
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void testSubmitRequestDurationCallsApiWithPayload() {
    MetricsApi metricsApi = mock(MetricsApi.class);
    MetricLoggerService mls =
        new MetricLoggerService(metricsApi, "", "", "", "", Collections.emptySet(), METRICS_PREFIX);
    mls.submitRequestDuration(100, 200, null);
    try {
      verify(metricsApi, times(1))
          .submitDistributionPointsAsync(ArgumentMatchers.any(DistributionPointsPayload.class));
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
