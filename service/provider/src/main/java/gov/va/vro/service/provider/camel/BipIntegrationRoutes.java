package gov.va.vro.service.provider.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * @author warren @Date 11/8/22
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class BipIntegrationRoutes extends RouteBuilder {

    public static final String ENDPOINT_UPDATE_CLAIM_STATUS = "direct:update-claim-status";

    @Override
    public void configure() throws Exception {

    }
}