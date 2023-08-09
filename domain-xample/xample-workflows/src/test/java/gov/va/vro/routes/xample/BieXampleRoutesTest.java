package gov.va.vro.routes.xample;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.test.BieMessagePayloadFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BieXampleRoutesTest extends CamelTestSupport {

  private static final String STARTING_URI = "seda:testQueue";
  private static final String TEST_QUEUE = "test";
  private static final String TEST_ROUTE_ID = TEST_QUEUE + "-saveToDb-route";

  @Mock private DbHelper dbHelper;

  @Override
  protected RoutesBuilder createRouteBuilder() {
    return new BieXampleRoutes(dbHelper, List.of(TEST_QUEUE));
  }

  // See https://camel.apache.org/manual/advice-with.html#_enabling_advice_during_testing
  @Override
  public boolean isUseAdviceWith() {
    return true;
  }

  @BeforeEach
  @SneakyThrows
  void mockEndpoints() {
    AdviceWith.adviceWith(
        context,
        TEST_ROUTE_ID,
        false,
        rb -> {
          // replace the rabbitmq with seda
          rb.replaceFromWith(STARTING_URI);
        });
    if (isUseAdviceWith()) context.start();
  }

  final BieMessagePayload testItem = BieMessagePayloadFactory.create();

  @Test
  @SneakyThrows
  void testSaveContentionEventRoute() {
    final BieMessagePayload response =
        template.requestBody(STARTING_URI, testItem, BieMessagePayload.class);
    assertThat(response.getEventType()).isEqualTo(testItem.getEventType());
    assertThat(response.getStatusMessage()).isNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getClaimId()).isEqualTo(testItem.getClaimId());
    assertThat(response.getContentionClassificationName())
        .isEqualTo(testItem.getContentionClassificationName());
    assertThat(response.getContentionId()).isEqualTo(testItem.getContentionId());
    assertThat(response.getContentionTypeCode())
        .isEqualTo(testItem.getContentionTypeCode());
    assertThat(response.getDiagnosticTypeCode()).isEqualTo(testItem.getDiagnosticTypeCode());
    assertThat(response.getEventDetails()).isEqualTo(testItem.getEventDetails());
    assertMockEndpointsSatisfied();
  }

  @Test
  @SneakyThrows
  void testMainRouteOnException() {
    final RuntimeException exception = new RuntimeException("Testing exception handling");
    Mockito.when(dbHelper.saveContentionEvent(Mockito.any())).thenThrow(exception);

    // send a message in the original route
    final BieMessagePayload response =
        template.requestBody(STARTING_URI, testItem, BieMessagePayload.class);
    assertThat(response.getEventType()).isEqualTo(testItem.getEventType());
    assertThat(response.getEventDetails()).isEqualTo(testItem.getEventDetails());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(response.getStatusMessage()).isEqualTo(exception.toString());

    assertMockEndpointsSatisfied();
  }
}
