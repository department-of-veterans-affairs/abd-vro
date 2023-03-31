package gov.va.vro.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.junit5.params.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.camel.BgsApiClientRoutes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
public class BgsApiClientRoutesTest extends CamelTestSupport {

    @EndpointInject("mock:direct:bgsClient-addNotes")
    MockEndpoint mockEndpoint;

    @Autowired
    @InjectMocks
    MasConfig masConfig;

    @Autowired
    @InjectMocks
    BgsApiClient bgsApiClient;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new BgsApiClientRoutes(template, bgsApiClient, masConfig);
    }

    @Test
    void testAddNotes() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived("test");
        template.sendBody("direct:bgsClient-addNotes", "test");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    void testAddNotesException() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived("fail");
        template.sendBody("direct:bgsClient-addNotes", "test");
        mockEndpoint.assertIsSatisfied();
    }

}
