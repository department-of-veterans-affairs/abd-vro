package gov.va.vro.service.provider.camel.processor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import gov.va.vro.service.provider.MasProcessingObjectTestData;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import gov.va.vro.service.spi.db.SaveToDbService;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class MasOrderExamProcessorTest {

  private IMasApiService masApiService;
  private SaveToDbService saveToDbService;
  private MasOrderExamProcessor processor;

  private final MasProcessingObject mpo =
      MasProcessingObjectTestData.builder().claimId("1234").build().create();
  private final CamelContext ctx = new DefaultCamelContext();
  private Exchange testExchange;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setup() {
    saveToDbService = Mockito.mock(SaveToDbService.class);
    masApiService = Mockito.mock(IMasApiService.class);
    processor = new MasOrderExamProcessor(masApiService, saveToDbService);
    testExchange = new DefaultExchange(ctx);
    final var log = (Logger) LoggerFactory.getLogger(MasOrderExamProcessor.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    log.addAppender(listAppender);
  }

  @Test
  void testSuccessfulExamOrder() {
    testExchange.getIn().setBody(mpo);
    processor.process(testExchange);
    Mockito.verify(masApiService, Mockito.times(1)).orderExam(Mockito.any());
    Mockito.verify(saveToDbService, Mockito.times(1))
        .insertOrUpdateExamOrderingStatus(Mockito.any());
    String message = listAppender.list.get(listAppender.list.size() - 1).getFormattedMessage();
    assertThat(message).contains("saving as ORDER_SUBMITTED");
  }

  @Test
  void testMasException() {
    Mockito.when(masApiService.orderExam(Mockito.any()))
        .thenThrow(new MasException("Mas Service Sent Error"));
    testExchange.getIn().setBody(mpo);
    try {
      processor.process(testExchange);
    } catch (MasException ignored) {
    }
    Mockito.verify(masApiService, Mockito.times(1)).orderExam(Mockito.any());
    Mockito.verify(saveToDbService, Mockito.never())
        .insertOrUpdateExamOrderingStatus(Mockito.any());
    String message = listAppender.list.get(listAppender.list.size() - 1).getFormattedMessage();
    assertThat(message).contains("Error in calling Order Exam API");
  }
}
