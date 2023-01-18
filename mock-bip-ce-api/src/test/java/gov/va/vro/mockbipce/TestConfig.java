package gov.va.vro.mockbipce;

import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@TestConfiguration
public class TestConfig {
  @Value("${http.client.ssl.trust-store}")
  private Resource trustStore;

  @Value("${http.client.ssl.trust-store-password}")
  private String trustStorePassword;

  @Value("${http.client.ssl.key-store}")
  private Resource keyStoreResource;

  @Value("${http.client.ssl.key-store-password}")
  private String keyStorePassword;

  @SneakyThrows
  @Bean(name = "httpsRestTemplate")
  public RestTemplate getHttpsRestTemplate(RestTemplateBuilder builder) {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());
    SSLContext sslContext = new SSLContextBuilder()
        .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
        .build();

    SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(requestFactory);
  }
}