package gov.va.vro.service.provider.camel.processor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import gov.va.vro.model.rrd.AbdEvidence;
import gov.va.vro.model.rrd.AbdEvidenceWithSummary;
import gov.va.vro.service.provider.MasProcessingObjectTestData;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HealthEvidenceProcessorTest {
  private HealthEvidenceProcessor processor;
  private final CamelContext ctx = new DefaultCamelContext();
  private Exchange testExchange;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setup() {
    processor = new HealthEvidenceProcessor();
    testExchange = new DefaultExchange(ctx);
    final var log = (Logger) LoggerFactory.getLogger(HealthEvidenceProcessor.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    log.addAppender(listAppender);
  }

  @Test
  void testProcess() {
    AbdEvidenceWithSummary evidence = new AbdEvidenceWithSummary();
    evidence.setEvidence(new AbdEvidence());
    MasProcessingObject mpo =
        MasProcessingObjectTestData.builder().claimId("1234").build().create();
    List<String> docsWoutAnnotsChecked = new ArrayList<>();
    docsWoutAnnotsChecked.add("doc1");
    testExchange.setProperty("docsWoutAnnotsChecked", docsWoutAnnotsChecked);
    testExchange.setProperty("payload", mpo);
    testExchange.getIn().setBody(evidence);
    processor.process(testExchange);
    String message = listAppender.list.get(0).getFormattedMessage();
    assertThat(message).contains(" MAS Processing >> Sufficient Evidence >>> null");
    var body = testExchange.getMessage().getBody();
    assertThat(body).isEqualTo(mpo);
    assertThat(mpo.getSufficientForFastTracking()).isNull();
    assertThat(mpo.getEvidence().getConditions()).isNotNull();
  }
}
