package gov.va.vro.service.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.mas.GeneratePdfResp;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasPollingProcessor implements Processor {
  private final CamelEntrance camelEntrance;
  private final MasDelays masDelays;
  private final MasCollectionService masCollectionService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    // TODO : Remove the extraneous log statements
    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);

    boolean isCollectionReady =
        masCollectionService.checkCollectionStatus(claimPayload.getCollectionId());

    if (isCollectionReady) {
      AbdEvidence abdEvidence = masCollectionService.getCollectionAnnotations(claimPayload);
      // call Lighthouse
      // Combine results and call PDF generation
      GeneratePdfResp generatePdfResp = generatePdf(claimPayload, abdEvidence);
      // call pcOrderExam in the absence of evidence
    } else {
      log.info("Collection {} is not ready. Requeue..ing...", claimPayload.getCollectionId());
      // re-request after some time
      camelEntrance.notifyAutomatedClaim(claimPayload, masDelays.getMasProcessingSubsequentDelay());
    }
  }

  public GeneratePdfResp generatePdf(MasAutomatedClaimPayload claimPayload, AbdEvidence abdEvidence)
      throws MasException {

    GeneratePdfPayload generatePdfPayload = new GeneratePdfPayload();
    generatePdfPayload.setEvidence(abdEvidence);
    generatePdfPayload.setClaimSubmissionId(claimPayload.getClaimDetail().getBenefitClaimId());
    generatePdfPayload.setDiagnosticCode(
        claimPayload.getClaimDetail().getConditions().getDiagnosticCode());
    VeteranInfo veteranInfo = new VeteranInfo();
    veteranInfo.setFirst(claimPayload.getFirstName());
    veteranInfo.setLast(claimPayload.getLastName());
    veteranInfo.setMiddle("");
    veteranInfo.setBirthdate(claimPayload.getDateOfBirth());
    generatePdfPayload.setVeteranInfo(veteranInfo);
    log.info(
        "Generating pdf for claim: {} and diagnostic code {}",
        generatePdfPayload.getClaimSubmissionId(),
        generatePdfPayload.getDiagnosticCode());
    try {
      log.info(generatePdfPayload.toString());
      String response = camelEntrance.generatePdf(generatePdfPayload);
      log.info(response);
      GeneratePdfResp pdfResponse = objectMapper.readValue(response, GeneratePdfResp.class);
      log.info(pdfResponse.toString());
      log.info("RESPONSE from generatePdf returned status: {}", pdfResponse.getStatus());
      if (pdfResponse.getStatus().equals("ERROR")) {
        log.info("RESPONSE from generatePdf returned error reason: {}", pdfResponse.getReason());
        String errMsg =
            "RESPONSE from generatePdf returned error reason: " + pdfResponse.getReason();
        throw new MasException(errMsg, new MasException());
      } else {
        return pdfResponse;
      }
    } catch (MasException | JsonProcessingException e) {
      log.error("Error in generate pdf", e);
      throw new MasException(e.getMessage(), e);
    }
  }
}
