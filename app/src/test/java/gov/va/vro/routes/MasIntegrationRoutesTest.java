package gov.va.vro.routes;

import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.MasTestData;
import gov.va.vro.model.mas.MasAnnotation;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasDocument;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import org.apache.camel.CamelExecutionException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

public class MasIntegrationRoutesTest extends BaseIntegrationTest {

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

    }
  }

  // TODO: test Mas API returns invalid object

  @Test
  void processClaimLighthouseConnectFailed() {
    int collectionId = 123;
    var collectionAnnotation = new MasCollectionAnnotation();
    MasDocument document = createHypertensionDocument();
    collectionAnnotation.setCollectionsId(123);
    collectionAnnotation.setDocuments(Collections.singletonList(document));
    Mockito.when(masApiService.getCollectionAnnotations(collectionId))
        .thenReturn(Collections.singletonList(collectionAnnotation));
    var payload = MasTestData.getMasAutomatedClaimPayload();
    try {
      camelEntrance.processClaim(payload);
      fail();
    } catch (CamelExecutionException me) {

    }
  }

  private static MasDocument createHypertensionDocument() {
    var document = new MasDocument();
    document.setCondition("Hypertension");
    var annotation = new MasAnnotation();
    annotation.setAnnotType("Medication");
    annotation.setAnnotVal("Placebo");
    document.setAnnotations(Collections.singletonList(annotation));
    return document;
  }
}
