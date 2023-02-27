package gov.va.vro.mocklh.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class MockBundles {
  private String observationBundle;
  private String medicationRequestBundle;
  private String conditionBundle;

  public static MockBundles of(Resource[] resources) throws IOException {
    MockBundles result = new MockBundles();
    for (Resource resource: resources) {
      InputStream stream = resource.getInputStream();
      String bundle = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
      String httpBundle = bundle.replace("//s", "");
      File file = resource.getFile();
      String name = file.getName();
      if (name.equals("Condition.json")) {
        result.setConditionBundle(httpBundle);
        continue;
      }
      if (name.equals("MedicationRequest.json")) {
        result.setMedicationRequestBundle(httpBundle);
        continue;
      }
      if (name.equals("Observation.json")) {
        result.setObservationBundle(httpBundle);
      }
    }
    return result;
  }
}
