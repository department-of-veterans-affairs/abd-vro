package gov.va.vro.routes.xample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BieXampleRoutesTest {

  @Mock private ObjectMapper objectMapper;

  @Mock private IMetricLoggerService metricLoggerService;
  private final String[] metricTagsSaveContentionEvent =
      new String[] {"type:saveContentionEvent", "source:xampleWorkflows"};

  @InjectMocks private BieXampleRoutes bieXampleRoutes;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleMessage_SuccessfulProcessing() throws Exception {
    // Arrange
    ContentionEventPayload payload = createSamplePayload();
    when(objectMapper.writeValueAsString(any(ContentionEventPayload.class)))
        .thenReturn("{\"claimId\":123,\"contentionId\":456}");

    // Act
    bieXampleRoutes.handleMessage(payload);

    // Assert
    verify(objectMapper, times(1)).writeValueAsString(payload);
    assertEquals(200, payload.getStatus());

    // Verify metrics logging
    verify(metricLoggerService, times(1))
        .submitCount(
            "vro_xample_workflows",
            IMetricLoggerService.METRIC.REQUEST_START,
            metricTagsSaveContentionEvent);
    verify(metricLoggerService, times(1))
        .submitRequestDuration(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong(),
            any());
    verify(metricLoggerService, times(1))
        .submitCount(
            "vro_xample_workflows",
            IMetricLoggerService.METRIC.RESPONSE_COMPLETE,
            metricTagsSaveContentionEvent);
  }

  @Test
  void handleMessage_ExceptionThrown() throws Exception {
    // Arrange
    ContentionEventPayload payload = createSamplePayload();
    Exception testException = new RuntimeException("Test exception");

    doThrow(testException).when(metricLoggerService).submitCount(any(), any(), any());
    when(objectMapper.writeValueAsString(any(ContentionEventPayload.class)))
        .thenReturn(
            "{\"claimId\":123,\"contentionId\":456,\"status\":500,\"statusMessage\":\"java.lang.RuntimeException: Test exception\"}");

    // Act
    bieXampleRoutes.handleMessage(payload);

    // Assert
    verify(objectMapper, times(1)).writeValueAsString(payload);
    assertEquals(500, payload.getStatus());
    assertEquals(testException.toString(), payload.getStatusMessage());

    // Verify metrics logging
    verify(metricLoggerService, times(1))
        .submitCount(
            "vro_xample_workflows",
            IMetricLoggerService.METRIC.RESPONSE_ERROR,
            metricTagsSaveContentionEvent);
  }

  private ContentionEventPayload createSamplePayload() {
    return ContentionEventPayload.builder()
        .claimId(123L)
        .contentionId(456L)
        .eventType(ContentionEvent.CONTENTION_ASSOCIATED)
        .notifiedAt(System.currentTimeMillis())
        .actionName("TestAction")
        .actionResultName("TestResult")
        .automationIndicator(true)
        .contentionTypeCode("TEST_CODE")
        .contentionStatusTypeCode("ACTIVE")
        .currentLifecycleStatus("IN_PROGRESS")
        .eventTime(System.currentTimeMillis())
        .benefitClaimTypeCode("BENEFIT_CODE")
        .description("Test Description")
        .actorStation("STATION_1")
        .actorUserId("USER_123")
        .details("Test Details")
        .veteranParticipantId(789L)
        .contentionClassificationName("Test Classification")
        .diagnosticTypeCode("DIAG_CODE")
        .journalStatusTypeCode("JOURNAL_STATUS")
        .dateAdded(System.currentTimeMillis())
        .dateCompleted(null)
        .dateUpdated(System.currentTimeMillis())
        .build();
  }
}
