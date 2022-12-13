package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasOrderExamConditions;
import gov.va.vro.model.mas.MasOrderExamReq;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasOrderExamProcessor implements Processor {

  private static final Map<String, String> MAS_CONDITION_CODE =
      Map.of("7101", "HYPERTENSION", "6602", "ASTHMA");

  private final IMasApiService masApiService;

  @Override
  public void process(Exchange exchange) {

    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);

    log.info("Ordering Exam for the collection {}.", claimPayload.getCollectionId());
    try {
      MasOrderExamReq masOrderExamReq = new MasOrderExamReq();
      masOrderExamReq.setCollectionsId(claimPayload.getCollectionId());
      MasOrderExamConditions masOrderExamConditions = new MasOrderExamConditions();
      masOrderExamConditions.setConditionCode(
          MAS_CONDITION_CODE.getOrDefault(claimPayload.getDiagnosticCode(), "NA"));
      masOrderExamConditions.setContentionText("HYPERTENSION");
      masOrderExamReq.setConditions(List.of(masOrderExamConditions));
      var response = masApiService.orderExam(masOrderExamReq);
      log.info("Order Exam Response :  " + response);
    } catch (MasException e) {
      log.error("Error in calling Order Exam API ", e);
      throw new MasException("Error in Order Exam API ", e);
    }
  }
}
