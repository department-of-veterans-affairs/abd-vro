package gov.va.vro.mockshared.rest;

import lombok.SneakyThrows;
import org.apache.catalina.connector.Connector;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyStore;
import javax.net.ssl.SSLContext;

public class RestUtil {
  /**
   * Get HTTP Rest template.
   *
   * @param builder builder
   * @param spec spec
   * @return Rest template request factory
   */
  @SneakyThrows
  public static RestTemplate getHttpsRestTemplate(RestTemplateBuilder builder, KeystoreSpec spec) {
    KeyStore keyStore = spec.getKeystore();
    KeyStore trustStore = spec.getTrustStore();

    SSLContext sslContext =
        new SSLContextBuilder()
            .loadTrustMaterial(trustStore, null)
            .loadKeyMaterial(keyStore, spec.getKeyStorePassword().toCharArray())
            .build();

    SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

    CloseableHttpClient httpClient =
        HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
    ClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(requestFactory);
  }

  /**
   * Get servlet container.
   *
   * @param httpPort HTTP port
   * @return tomcat webserver factory
   */
  public static ServletWebServerFactory getServletContainer(int httpPort) {
    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    connector.setPort(httpPort);

    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    tomcat.addAdditionalTomcatConnectors(connector);
    return tomcat;
  }
}
