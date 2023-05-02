package gov.va.vro.service.provider.camel.processor;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import gov.va.vro.model.rrd.AbdEvidenceWithSummary;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AssessmentResultProcessorTest {

    private SaveToDbService saveToDbService;
    private AssessmentResultProcessor processor;

    private final CamelContext ctx = new DefaultCamelContext();
    private Exchange testExchange;

    private final static String diagnosticCode = "7101";
    private final static UUID claimId = UUID.randomUUID();

    private final static AbdEvidenceWithSummary evidence = new AbdEvidenceWithSummary();
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        saveToDbService = Mockito.mock(SaveToDbService.class);
        processor = new AssessmentResultProcessor(saveToDbService);
        testExchange = new DefaultExchange(ctx);
        final var log = (Logger) LoggerFactory.getLogger(AssessmentResultProcessor.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);
    }

    @Test
    void testMissingClaimId(){
        testExchange.setProperty("diagnosticCode", diagnosticCode);
        testExchange.getIn().setBody(evidence);
        processor.process(testExchange);
        Mockito.verify(saveToDbService, Mockito.never()).insertAssessmentResult(Mockito.any(), Mockito.any(), Mockito.any());
        String message = listAppender.list.get(0).getFormattedMessage();
        assertThat(message).contains("Claim Id was empty, exiting");
    }

    @Test
    void testMissingDiagnosticCode(){
        testExchange.setProperty("claim-id", UUID.randomUUID());
        testExchange.getIn().setBody(evidence);
        processor.process(testExchange);
        Mockito.verify(saveToDbService, Mockito.never()).insertAssessmentResult(Mockito.any(), Mockito.any(), Mockito.any());
        String message = listAppender.list.get(0).getFormattedMessage();
        assertThat(message).contains("Diagnostic Code was empty, exiting.");
    }

    @Test
    void testEmptyEvidence(){
        testExchange.setProperty("claim-id", claimId);
        testExchange.setProperty("diagnosticCode", diagnosticCode);
        processor.process(testExchange);
        Mockito.verify(saveToDbService, Mockito.never()).insertAssessmentResult(Mockito.any(), Mockito.any(), Mockito.any());
        String message = listAppender.list.get(0).getFormattedMessage();
        assertThat(message).contains("Evidence was empty, exiting");
    }

    @Test
    void testSuccessfulSave(){
        testExchange.setProperty("claim-id", claimId);
        testExchange.setProperty("diagnosticCode", diagnosticCode);
        testExchange.getIn().setBody(evidence);
        processor.process(testExchange);
        Mockito.verify(saveToDbService, Mockito.times(1)).insertAssessmentResult(claimId, evidence, diagnosticCode);
    }


}
