package gov.va.vro.service;

import gov.va.vro.end2end.VroV2Tests;
import gov.va.vro.end2end.util.AutomatedClaimTestSpec;
import gov.va.vro.model.bgs.BgsApiClientDto;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.spi.model.Claim;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.camel.test.junit5.params.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BgsApiClientTest extends VroV2Tests{

//   @Mock
//   private EvidenceSummaryDocumentRepository esDocRepository;


    @Autowired
    private MasProcessingObject masProcessingObject;


  @Test
  public void testBuildRequestForReadyForDecision() {

    String collectionId = "375";
    AutomatedClaimTestSpec spec = specFor200(collectionId);
    testAutomatedClaimFullPositive(spec);

  }

  @Test
  public void testBuildRequestForExamOrder() {

    String collectionId = "377";
    AutomatedClaimTestSpec spec = specFor200(collectionId);
    testAutomatedClaimOrderExam(spec);

  }

  @Test
  public void testBuildRequestForOffRampSufficiencyUndermined() {
    
    String collectionId = "500";
    AutomatedClaimTestSpec spec = specFor200(collectionId);
    testAutomatedClaimOffRamp(spec);
    testClaimSufficientStatus(collectionId, null);

  }

  @Test
  public void testBuildRequestForOffRampNewNotPresumptive() {
    
    MasAutomatedClaimPayload payload = masProcessingObject.getClaimPayload();
    assertTrue(payload.getOffRampError().equals("newNotPresumptive"));

  }

  @Test
  public void testBuildRequestForOffRampPdfUploadFailed() {

    MasAutomatedClaimPayload payload = masProcessingObject.getClaimPayload();
    assertTrue(payload.getOffRampError().equals("docUploadFailed"));

  }
       
}
