package gov.va.vro.mockbipclaims.configuration;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Opens an http port. */
@Component
@Profile("!test")
public class HttpServer {
  /**
   * Opens an additional Http port to provide access without TLS.
   *
   * @param httpPort Port number
   * @return Servlet Web Server Factory
   */
  @Bean
  public ServletWebServerFactory servletContainer(@Value("${server.http.port}") int httpPort) {
    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    connector.setPort(httpPort);

    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    tomcat.addAdditionalTomcatConnectors(connector);
    return tomcat;
  }
}
