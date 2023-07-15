package gov.va.vro.routes.xample;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.vro.biekafka.model.BieMessagePayload;
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

import java.time.LocalDateTime;
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

  final BieMessagePayload testItem =
      BieMessagePayload.builder()
          .event("testEvent")
          .eventDetails("testEventDetails")
          .notifiedAt(LocalDateTime.now().toString())
          .build();

  @Test
  @SneakyThrows
  void testSaveContentionEventRoute() {
    final BieMessagePayload response =
        template.requestBody(STARTING_URI, testItem, BieMessagePayload.class);
    assertThat(response.getEvent()).isEqualTo(testItem.getEvent());
    assertThat(response.getEventDetails()).isEqualTo(testItem.getEventDetails());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getStatusMessage()).isNull();

    assertMockEndpointsSatisfied();
  }

  @Test
  @SneakyThrows
  void testMainRouteOnException() {
    final IllegalStateException exception = new IllegalStateException("Testing exception handling");
    Mockito.when(dbHelper.saveContentionEvent(Mockito.any())).thenThrow(exception);

    // send a message in the original route
    final BieMessagePayload response =
        template.requestBody(STARTING_URI, testItem, BieMessagePayload.class);
    assertThat(response.getEvent()).isEqualTo(testItem.getEvent());
    assertThat(response.getEventDetails()).isEqualTo(testItem.getEventDetails());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(response.getStatusMessage()).isEqualTo(exception.toString());

    assertMockEndpointsSatisfied();
  }
}
