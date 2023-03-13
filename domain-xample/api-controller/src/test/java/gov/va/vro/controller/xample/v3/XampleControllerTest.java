package gov.va.vro.controller.xample.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import gov.va.vro.api.xample.ResourceException;
import gov.va.vro.api.xample.v3.ResourceRequest;
import gov.va.vro.api.xample.v3.ResourceResponse;
import gov.va.vro.camel.CamelEntry;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class XampleControllerTest {
  @Mock CamelEntry camelEntry;
  ResourceMapper resourceMapper = new ResourceMapperImpl();

  void mockCamelEntryResult(SomeDtoModel camelResult) {
    when(camelEntry.inOut(anyString(), anyString(), any(), any())).thenReturn(camelResult);
  }

  @Test
  @SneakyThrows
  void testPostResource() {
    var camelResult =
        SomeDtoModel.builder()
            .resourceId("320")
            .diagnosticCode("A")
            .status(StatusValue.PROCESSING.name())
            .build();
    mockCamelEntryResult(camelResult);

    var controller = new XampleController(camelEntry, resourceMapper);
    ResourceRequest request =
        new ResourceRequest(camelResult.getResourceId(), camelResult.getDiagnosticCode());
    ResponseEntity<ResourceResponse> respEntity = controller.postResource(request);

    assertEquals(HttpStatus.CREATED, respEntity.getStatusCode());
    assertEquals(request.getResourceId(), respEntity.getBody().getResourceId());
    assertEquals(StatusValue.PROCESSING.name(), respEntity.getBody().getStatus());
  }

  @Test
  @SneakyThrows
  void testPostResourceWithErrorStatus() {
    var camelResult =
        SomeDtoModel.builder()
            .resourceId("320")
            .diagnosticCode("A")
            .status(StatusValue.ERROR.name())
            .statusMessage("Some Exception was thrown")
            .build();
    mockCamelEntryResult(camelResult);

    var controller = new XampleController(camelEntry, resourceMapper);
    ResourceRequest request =
        new ResourceRequest(camelResult.getResourceId(), camelResult.getDiagnosticCode());
    ResponseEntity<ResourceResponse> respEntity = controller.postResource(request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respEntity.getStatusCode());
    assertEquals(request.getResourceId(), respEntity.getBody().getResourceId());
    assertEquals(StatusValue.ERROR.name(), respEntity.getBody().getStatus());
  }

  @Test
  @SneakyThrows
  void testPostResourceWithException() {
    final String exceptionMsg = "Test exception";
    when(camelEntry.inOut(anyString(), anyString(), any(), any()))
        .thenThrow(new RuntimeException(exceptionMsg));

    var controller = new XampleController(camelEntry, resourceMapper);
    ResourceRequest request = new ResourceRequest("320", "A");
    try {
      controller.postResource(request);
      fail("Expected ResourceException");
    } catch (ResourceException re) {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, re.getHttpStatus());
      assertEquals(request.getResourceId(), re.getResourceId());
      assertEquals("java.lang.RuntimeException: " + exceptionMsg, re.getMessage());
    }
  }

  @Test
  @SneakyThrows
  void testGetResource() {
    var camelResult =
        SomeDtoModel.builder()
            .resourceId("320")
            .diagnosticCode("A")
            .status(StatusValue.DONE.name())
            .build();
    mockCamelEntryResult(camelResult);

    var controller = new XampleController(camelEntry, resourceMapper);
    ResponseEntity<SomeDtoModel> respEntity = controller.getResource(camelResult.getResourceId());

    assertEquals(HttpStatus.OK, respEntity.getStatusCode());
    assertEquals(camelResult.getResourceId(), respEntity.getBody().getResourceId());
    assertEquals(camelResult.getStatus(), respEntity.getBody().getStatus());
  }

  @Test
  @SneakyThrows
  void testGetResourceWithUnknownId() {
    var camelResult =
        SomeDtoModel.builder()
            .resourceId("320")
            .diagnosticCode("A")
            .status(StatusValue.NOT_FOUND.name())
            .build();
    mockCamelEntryResult(camelResult);

    var controller = new XampleController(camelEntry, resourceMapper);
    ResponseEntity<SomeDtoModel> respEntity = controller.getResource(camelResult.getResourceId());

    assertEquals(HttpStatus.NOT_FOUND, respEntity.getStatusCode());
    assertEquals(camelResult.getResourceId(), respEntity.getBody().getResourceId());
    assertEquals(StatusValue.NOT_FOUND.name(), respEntity.getBody().getStatus());
  }

  @Test
  @SneakyThrows
  void testGetResourceWithErrorStatus() {
    var camelResult =
        SomeDtoModel.builder()
            .resourceId("320")
            .diagnosticCode("A")
            .status(StatusValue.ERROR.name())
            .statusMessage("Some Exception was thrown")
            .build();
    mockCamelEntryResult(camelResult);

    var controller = new XampleController(camelEntry, resourceMapper);
    ResponseEntity<SomeDtoModel> respEntity = controller.getResource(camelResult.getResourceId());

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respEntity.getStatusCode());
    assertEquals(camelResult.getResourceId(), respEntity.getBody().getResourceId());
    assertEquals(StatusValue.ERROR.name(), respEntity.getBody().getStatus());
  }

  @Test
  @SneakyThrows
  void testGetResourceWithException() {
    final String exceptionMsg = "Test exception";
    when(camelEntry.inOut(anyString(), anyString(), any(), any()))
        .thenThrow(new RuntimeException(exceptionMsg));

    var controller = new XampleController(camelEntry, resourceMapper);
    try {
      controller.getResource("320");
      fail("Expected ResourceException");
    } catch (ResourceException re) {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, re.getHttpStatus());
      assertEquals("320", re.getResourceId());
      assertEquals("java.lang.RuntimeException: " + exceptionMsg, re.getMessage());
    }
  }
}
