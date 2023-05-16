package gov.va.vro.routes;

import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.MasTestData;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.rrd.mas.MasCollectionAnnotation;
import gov.va.vro.model.rrd.mas.MasDocument;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelExecutionException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

@Slf4j
public class MasIntegrationNegativeTest extends BaseIntegrationTest {

  @Autowired CamelEntrance camelEntrance;

  @MockBean IMasApiService masApiService;

  @Autowired @InjectMocks MasCollectionService masCollectionService;

  @Test
  void processClaimInvalidInput() {
    var payload = MasAutomatedClaimPayload.builder().collectionId(123).build();
    try {
      camelEntrance.processClaim(payload);
      fail();
    } catch (CamelExecutionException cee) {
      log.info("error in masIntegrationNegativeTest.");
    }
  }

  @Test
  void processClaimLighthouseConnectFailed() {
    int collectionId = 123;
    var collectionAnnotation = new MasCollectionAnnotation();
    MasDocument document = MasTestData.createHypertensionDocument();
    collectionAnnotation.setCollectionsId(123);
    collectionAnnotation.setDocuments(Collections.singletonList(document));
    Mockito.when(masApiService.getCollectionAnnotations(collectionId))
        .thenReturn(Collections.singletonList(collectionAnnotation));
    var payload = MasTestData.getMasAutomatedClaimPayload();
    payload.getClaimDetail().getConditions().setDiagnosticCode("1233");
    try {
      camelEntrance.processClaim(payload);
      fail();
    } catch (CamelExecutionException me) {
      log.info("error in masIntegrationNegativeTest.");
    }
  }
}
