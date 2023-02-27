package gov.va.vro.mocklh.model;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Getter
@Setter
public class MockBundles {
  private static final String[] RESOURCE_TYPES = {"Condition", "MedicationRequest", "Observation"};

  private String observationBundle;
  private String medicationRequestBundle;
  private String conditionBundle;

  @SneakyThrows
  private String resourceToString(String path) {
    var io = this.getClass().getClassLoader().getResourceAsStream(path);
    try (Reader reader = new InputStreamReader(io)) {
      return FileCopyUtils.copyToString(reader);
    }
  }

  public static MockBundles of(String basePath) throws IOException {
    MockBundles result = new MockBundles();
    for (String resourceType : RESOURCE_TYPES) {
      String path = String.format("%s/%s.json", basePath, resourceType);
      String bundle = result.resourceToString(path);
      String httpBundle = bundle.replace("//s", "");
      if (resourceType.equals("Condition")) {
        result.setConditionBundle(httpBundle);
        continue;
      }
      if (resourceType.equals("MedicationRequest")) {
        result.setMedicationRequestBundle(httpBundle);
        continue;
      }
      if (resourceType.equals("Observation")) {
        result.setObservationBundle(httpBundle);
      }
    }
    return result;
  }
}
