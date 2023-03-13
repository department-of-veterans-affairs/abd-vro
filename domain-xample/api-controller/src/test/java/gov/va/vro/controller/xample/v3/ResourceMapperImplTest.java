package gov.va.vro.controller.xample.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.vro.api.xample.v3.ResourceRequest;
import gov.va.vro.api.xample.v3.ResourceResponse;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import org.junit.jupiter.api.Test;

public class ResourceMapperImplTest {
  ResourceMapperImpl impl = new ResourceMapperImpl();

  @Test
  void testToModel() {
    ResourceRequest request = new ResourceRequest("7703", "A");
    SomeDtoModel model = impl.toModel(request);
    assertEquals(request.getResourceId(), model.getResourceId());
    assertEquals(request.getDiagnosticCode(), model.getDiagnosticCode());
  }

  @Test
  void testToModelWithNullRequest() {
    assertNull(impl.toModel(null));
  }

  @Test
  void testToModelWithInvalidRequest() {

    {
      ResourceRequest request = new ResourceRequest(null, null);
      var exception = assertThrows(NullPointerException.class, () -> impl.toModel(request));
      assertEquals("resourceId is marked non-null but is null", exception.getMessage());
    }
    {
      ResourceRequest request = new ResourceRequest("7703", null);
      var exception = assertThrows(NullPointerException.class, () -> impl.toModel(request));
      assertEquals("diagnosticCode is marked non-null but is null", exception.getMessage());
    }
  }

  @Test
  void testToResourceResponse() {
    SomeDtoModel model = new SomeDtoModel("320", "A", StatusValue.PROCESSING.name(), null);

    ResourceResponse response = impl.toResourceResponse(model);
    assertEquals(response.getResourceId(), model.getResourceId());
    assertEquals(response.getDiagnosticCode(), model.getDiagnosticCode());
    assertEquals(response.getStatus(), model.getStatus());
  }

  @Test
  void testToResourceResponseWithNullModel() {
    assertNull(impl.toResourceResponse(null));
  }
}
