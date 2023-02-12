package gov.va.vro.mockbipce.config;

import gov.va.vro.mockshared.RestUtil;
import org.springframework.beans.factory.annotation.Value;
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
    return RestUtil.getServletContainer(httpPort);
  }
}
