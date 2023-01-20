package gov.va.vro.mockbipce;

import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Base64;

@TestConfiguration
public class TestConfig {
  @Value("${http.client.ssl.trust-store-password}")
  private String trustStorePassword;

  @Value("${http.client.ssl.key-store-password}")
  private String keyStorePassword;

  @Value("${vro-mock-bip-ce-truststore}")
  private String trustStoreBase64;

  @Value("${vro-mock-bip-ce-keystore}")
  private String keyStoreBase64;

  @SneakyThrows
  private KeyStore getKeyStore(String base64, String password) {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    String noSpaceBase64 = base64.replaceAll("\\s+", "");
    byte[] decodedBytes = Base64.getDecoder().decode(noSpaceBase64);
    InputStream stream = new ByteArrayInputStream(decodedBytes);
    keyStore.load(stream, password.toCharArray());
    return keyStore;
  }

  @SneakyThrows
  @Bean(name = "httpsRestTemplate")
  public RestTemplate getHttpsRestTemplate(RestTemplateBuilder builder) {
    KeyStore keyStore = getKeyStore(keyStoreBase64, keyStorePassword);
    KeyStore trustStore = getKeyStore(trustStoreBase64, trustStorePassword);

    SSLContext sslContext = new SSLContextBuilder()
        .loadTrustMaterial(trustStore, null)
        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
        .build();

    SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(requestFactory);
  }
  @SneakyThrows
  @Bean(name = "httpsNoCertificationRestTemplate")
  public RestTemplate getHttpsNoCertificationRestTemplate(RestTemplateBuilder builder) {
    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
    KeyStore keyStore = getKeyStore(keyStoreBase64, keyStorePassword);
    SSLContext sslContext = new SSLContextBuilder()
        .loadTrustMaterial(null, acceptingTrustStrategy)
        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
        .build();

    SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(requestFactory);
  }
}