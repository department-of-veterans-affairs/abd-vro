package gov.va.vro.service.provider;

import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.rrd.mas.MasOrderExamConditions;
import gov.va.vro.model.rrd.mas.request.MasOrderExamRequest;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.ExamOrder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

  private final IMasApiService masApiService;

  private static final Map<String, String> MAS_CONDITION_CODES =
      Map.of("7101", "HYPERTENSION", "6602", "ASTHMA");

  private static final String SUBMITTED_STATUS = "ORDER_SUBMITTED";
  private final SaveToDbService saveToDbService;

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    var claimPayload = exchange.getMessage().getBody(MasProcessingObject.class);

    log.info("Ordering Exam for the collection {}.", claimPayload.getCollectionId());
    try {
      MasOrderExamRequest masOrderExamRequest = new MasOrderExamRequest();
      masOrderExamRequest.setCollectionsId(claimPayload.getCollectionId());
      MasOrderExamConditions masOrderExamConditions = new MasOrderExamConditions();
      masOrderExamConditions.setConditionCode(
          MAS_CONDITION_CODES.getOrDefault(claimPayload.getDiagnosticCode(), "NA"));
      masOrderExamConditions.setContentionText(
          MAS_CONDITION_CODES.getOrDefault(claimPayload.getDiagnosticCode(), "NA"));
      masOrderExamRequest.setConditions(List.of(masOrderExamConditions));
      var response = masApiService.orderExam(masOrderExamRequest);
      log.info("Order Exam Response :  " + response + " saving as ORDER_SUBMITTED");
      ExamOrder examOrder =
          ExamOrder.builder()
              .collectionId(Integer.toString(claimPayload.getCollectionId()))
              .idType(MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE)
              .status(SUBMITTED_STATUS)
              .build();
      saveToDbService.insertOrUpdateExamOrderingStatus(examOrder);
      exchange.setProperty("orderExamResponse", response);

    } catch (MasException e) {
      log.error("Error in calling Order Exam API ", e);
      throw new MasException("Error in Order Exam API ", e);
    }
  }
}
