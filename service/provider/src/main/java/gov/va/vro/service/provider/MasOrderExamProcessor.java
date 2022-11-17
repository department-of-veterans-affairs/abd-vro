package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasOrderExamConditions;
import gov.va.vro.model.mas.MasOrderExamReq;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasOrderExamProcessor implements Processor {

  private final IMasApiService masApiService;

  private static Map<String, String> masConditionCode =
      Map.of("7101", "HYPERTENSION", "6602", "ASTHMA");

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);

    log.info("Ordering Exam for the collection {}.", claimPayload.getCollectionId());
    try {
      MasOrderExamReq masOrderExamReq = new MasOrderExamReq();
      masOrderExamReq.setCollectionsId(claimPayload.getCollectionId());
      MasOrderExamConditions masOrderExamConditions = new MasOrderExamConditions();
      masOrderExamConditions.setConditionCode(
          masConditionCode.getOrDefault(claimPayload.getDiagnosticCode(), "NA"));
      masOrderExamConditions.setContentionText("HYPERTENSION");
      ArrayList<MasOrderExamConditions> listMasOrderExamConditions =
          new ArrayList<MasOrderExamConditions>();
      listMasOrderExamConditions.add(masOrderExamConditions);
      masOrderExamReq.setConditions(listMasOrderExamConditions);
      var response = masApiService.orderExam(masOrderExamReq);
      log.info("Order Exam Response :  " + response);
      exchange.setProperty("OrderExamResponse", response);

    } catch (MasException e) {
      log.error("Error in calling Order Exam API ", e);
      throw new MasException("Error in Order Exam API ", e);
    }
  }
}
