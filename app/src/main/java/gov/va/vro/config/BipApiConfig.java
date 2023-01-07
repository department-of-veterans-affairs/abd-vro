package gov.va.vro.config;

import gov.va.vro.service.provider.BipApiProps;
import gov.va.vro.service.provider.bip.BipException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;

/**
 * Configures BIP API access.
 *
 * @author warren @Date 10/31/22
 */
@Configuration
@Slf4j
@Setter
public class BipApiConfig {

  @Value("${truststore}")
  private String trustStore;

  @Value("${truststore_password}")
  private String password;

  @Bean
  public BipApiProps getBipApiProps() {
    return new BipApiProps();
  }

  @Bean(name = "bipCERestTemplate")
  public RestTemplate getHttpsRestTemplate(RestTemplateBuilder builder) throws BipException {
    try { // TODO: keep log for testing, remove it later.
      log.info("truststore: {}, password: {}", trustStore, password);
      URL url = getClass().getClassLoader().getResource(trustStore);

      SSLContext sslContext =
          new SSLContextBuilder().loadTrustMaterial(url, password.toCharArray()).build();
      SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
      HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
      return builder
          .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
          .build();
    } catch (IOException
        | CertificateException
        | NoSuchAlgorithmException
        | KeyStoreException
        | KeyManagementException e) {
      log.error("Failed to create SSL context for VA certificate. {}", e.getMessage(), e);
      throw new BipException("Failed to create SSL context.", e);
    } catch (Exception e) {
      log.error("Unexpected error.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  @Primary
  @Bean(name = "bipRestTemplate")
  public RestTemplate restTemplate() throws BipException {
    try {
      TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

      SSLContext sslContext =
          org.apache.http.ssl.SSLContexts.custom()
              .loadTrustMaterial(null, acceptingTrustStrategy)
              .build();

      SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

      CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

      HttpComponentsClientHttpRequestFactory requestFactory =
          new HttpComponentsClientHttpRequestFactory();

      requestFactory.setHttpClient(httpClient);
      return new RestTemplate(requestFactory);
    } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
      log.error("failed to configure bipRestTemplate. {}", e.getMessage());
      throw new BipException("Configuration failed for restTemplate.", e);
    }
  }
}
