package gov.va.vro.consolegroovy

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:foo").to("log:bar");
    }
}
