package gov.va.vro.routes.xample;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.ContentionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BieXampleRoutesTest {
  @Mock private DbHelper dbHelper;

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
    BieMessagePayload payload = createSamplePayload();
    when(objectMapper.writeValueAsString(any(BieMessagePayload.class)))
        .thenReturn("{\"claimId\":123,\"contentionId\":456}");

    // Act
    bieXampleRoutes.handleMessage(payload);

    // Assert
    verify(dbHelper, times(1)).saveContentionEvent(payload);
    verify(objectMapper, times(1)).writeValueAsString(payload);
    assertEquals(200, payload.getStatus());

    // Verify metrics logging
    verify(metricLoggerService, times(1))
        .submitCount(IMetricLoggerService.METRIC.REQUEST_START, metricTagsSaveContentionEvent);
    verify(metricLoggerService, times(1))
        .submitRequestDuration(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    verify(metricLoggerService, times(1))
        .submitCount(IMetricLoggerService.METRIC.RESPONSE_COMPLETE, metricTagsSaveContentionEvent);
  }

  @Test
  void handleMessage_ExceptionThrown() throws Exception {
    // Arrange
    BieMessagePayload payload = createSamplePayload();
    Exception testException = new RuntimeException("Test exception");

    doThrow(testException).when(dbHelper).saveContentionEvent(payload);
    when(objectMapper.writeValueAsString(any(BieMessagePayload.class)))
        .thenReturn(
            "{\"claimId\":123,\"contentionId\":456,\"status\":500,\"statusMessage\":\"java.lang.RuntimeException: Test exception\"}");

    // Act
    bieXampleRoutes.handleMessage(payload);

    // Assert
    verify(dbHelper, times(1)).saveContentionEvent(payload);
    verify(objectMapper, times(1)).writeValueAsString(payload);
    assertEquals(500, payload.getStatus());
    assertEquals(testException.toString(), payload.getStatusMessage());

    // Verify metrics logging
    verify(metricLoggerService, times(1))
        .submitCount(IMetricLoggerService.METRIC.RESPONSE_ERROR, metricTagsSaveContentionEvent);
  }

  private BieMessagePayload createSamplePayload() {
    return BieMessagePayload.builder()
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
