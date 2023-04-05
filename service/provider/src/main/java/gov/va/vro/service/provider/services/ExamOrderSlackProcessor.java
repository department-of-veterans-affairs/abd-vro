package gov.va.vro.service.provider.services;

import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.service.provider.mas.MasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@Slf4j
public class ExamOrderSlackProcessor implements Processor {
  @Override
  public void process(Exchange exchange) {
    ExamOrdersInfo exams = exchange.getIn().getBody(ExamOrdersInfo.class);
    exchange
        .getIn()
        .setBody(AuditEvent.fromAuditable(exams, "exam-order-slack", getSlackMessage(exams)));
  }

  public static String getSlackMessage(ExamOrdersInfo exams) {
    StringBuilder msg = new StringBuilder();
    if (exams != null) {
      for (ExamOrderInfoResponse exam : exams.getExamOrderInfoList()) {
        msg.append("[ExamOrder")
            .append(" collection ID: ")
            .append(exam.getCollectionId())
            .append(" createdAt: ")
            .append(exam.getCreatedAt())
            .append(" status: ")
            .append(exam.getStatus())
            .append("], ");
      }
    } else {
      log.error("No exam orders were available to slack.");
      throw new MasException("No exam orders were available to slack.");
    }
    return msg.toString();
  }
}
