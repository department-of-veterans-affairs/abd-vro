package gov.va.vro.mocklh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
public class StoreTest {
  @Autowired private MockBundleStore store;

  @Autowired ObjectMapper mapper;

  @Test
  @SneakyThrows
  void testMock1012666073V986297Observation() {
    String observation = store.getMockObservationBundle("mock1012666073V986297");
    assertNotNull(observation);

    JsonNode json = mapper.readTree(observation);
    JsonNode total = json.get("total");
    assertEquals(10, total.asInt());
  }

  @Test
  @SneakyThrows
  void testMock1012666073V986297MedicationRequest() {
    String medicationRequest = store.getMockMedicationRequestBundle("mock1012666073V986297");
    assertNotNull(medicationRequest);

    JsonNode json = mapper.readTree(medicationRequest);
    JsonNode total = json.get("total");
    assertEquals(10, total.asInt());
  }

  @Test
  @SneakyThrows
  void testMock1012666073V986297Condition() {
    String condition = store.getMockConditionBundle("mock1012666073V986297");
    assertNotNull(condition);

    JsonNode json = mapper.readTree(condition);
    JsonNode total = json.get("total");
    assertEquals(3, total.asInt());
  }
}
