package gov.va.vro.bip.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipPayloadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

class BipRequestErrorHandlerTest {
  private static final String CLAIM_RESPONSE_404 = "bip-test-data/claim_response_404.json";

  enum HttpStatusCodeTestCase {
    NOT_FOUND(new HttpClientErrorException(HttpStatus.NOT_FOUND)),
    BAD_REQUEST(new HttpClientErrorException(HttpStatus.NOT_FOUND)),
    SERVER_EXCEPTION(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    private final int statusCode;
    private final String statusReason;
    private final HttpStatusCodeException exception;

    HttpStatusCodeTestCase(HttpStatusCodeException exception) {
      this.exception = exception;
      this.statusCode = exception.getStatusCode().value();
      this.statusReason = HttpStatus.valueOf(exception.getStatusCode().value()).name();
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpStatusCodeTestCase.class)
  public void testHandleError_ClientOrServerErrors(HttpStatusCodeTestCase test) {
    BipRequestErrorHandler handler = new BipRequestErrorHandler(new ObjectMapper());

    Object response =
        handler.handleError(
            null, null, new ListenerExecutionFailedException("test", test.exception));

    if (response instanceof BipPayloadResponse bipPayloadResponse) {
      assertEquals(test.statusCode, bipPayloadResponse.getStatusCode());
      assertEquals(test.statusReason, bipPayloadResponse.getStatusMessage());
    }
  }

  @Test
  public void testHandleError_BipException() {
    BipRequestErrorHandler handler = new BipRequestErrorHandler(new ObjectMapper());

    BipException cause = new BipException("Oops");
    Object response =
        handler.handleError(null, null, new ListenerExecutionFailedException("test", cause));

    if (response instanceof BipPayloadResponse bipPayloadResponse) {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), bipPayloadResponse.getStatusCode());
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), bipPayloadResponse.getStatusMessage());
    }
  }

  @Test
  public void testHandleError_InvalidResponseBody() throws Exception {
    ObjectMapper mapper = mock(ObjectMapper.class);
    when(mapper.readValue(anyString(), eq(BipPayloadResponse.class)))
        .thenThrow(JsonProcessingException.class);
    BipRequestErrorHandler handler = new BipRequestErrorHandler(mapper);

    Object response =
        handler.handleError(
            null,
            null,
            new ListenerExecutionFailedException(
                "test", new HttpClientErrorException(HttpStatus.NOT_FOUND)));

    if (response instanceof BipPayloadResponse bipPayloadResponse) {
      assertEquals(HttpStatus.NOT_FOUND.value(), bipPayloadResponse.getStatusCode());
      assertEquals(HttpStatus.NOT_FOUND.name(), bipPayloadResponse.getStatusMessage());
    }
  }

  @Test
  public void testHandleError_ValidResponseBody() throws Exception {
    BipRequestErrorHandler handler = new BipRequestErrorHandler(new ObjectMapper());

    String resp404Body = getTestData(CLAIM_RESPONSE_404);

    HttpClientErrorException cause =
        new HttpClientErrorException(
            HttpStatus.NOT_FOUND,
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            resp404Body.getBytes(),
            Charset.defaultCharset());
    Object response =
        handler.handleError(null, null, new ListenerExecutionFailedException("test", cause));

    if (response instanceof BipPayloadResponse bipPayloadResponse) {
      assertEquals(HttpStatus.NOT_FOUND.value(), bipPayloadResponse.getStatusCode());
      assertEquals(HttpStatus.NOT_FOUND.name(), bipPayloadResponse.getStatusMessage());
      assertFalse(bipPayloadResponse.getMessages().isEmpty());
    }
  }

  private String getTestData(String dataFile) throws Exception {
    String filename =
        Objects.requireNonNull(getClass().getClassLoader().getResource(dataFile)).getPath();
    Path filePath = Path.of(filename);
    return Files.readString(filePath);
  }
}
