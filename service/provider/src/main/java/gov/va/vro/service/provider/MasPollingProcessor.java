package gov.va.vro.service.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.mas.GeneratePdfResp;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
// TODO: Turn this into a Slip router  or conditional
public class MasPollingProcessor implements Processor {
  private final CamelEntrance camelEntrance;
  private final MasDelays masDelays;
  private final MasCollectionService masCollectionService;
  private final ProducerTemplate producerTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);

    boolean isCollectionReady =
        masCollectionService.checkCollectionStatus(claimPayload.getCollectionId());

    if (isCollectionReady) {
      // TODO:  call Lighthouse
      // Combine results and call PDF generation
      GeneratePdfPayload generatePdfPayload =
          producerTemplate.requestBody(
              MasIntegrationRoutes.ENDPOINT_MAS_PROCESSING, claimPayload, GeneratePdfPayload.class);
      GeneratePdfResp generatePdfResp = generatePdf(generatePdfPayload);
      // TODO: call pcOrderExam in the absence of evidence
    } else {
      log.info("Collection {} is not ready. Requeue..ing...", claimPayload.getCollectionId());
      // re-request after some time
      camelEntrance.notifyAutomatedClaim(claimPayload, masDelays.getMasProcessingSubsequentDelay());
    }
  }

  // TODO: move to a separate route

  public GeneratePdfResp generatePdf(GeneratePdfPayload generatePdfPayload) throws MasException {

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
