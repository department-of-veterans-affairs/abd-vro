package gov.va.vro.bip.config;

import gov.va.vro.bip.service.BipApiProps;
import gov.va.vro.bip.service.BipException;
import gov.va.vro.bip.service.ClaimProps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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

  @Value("${keystore}")
  private String keystore;

  @Bean
  public BipApiProps getBipApiProps() {
    return new BipApiProps();
  }

  @Bean
  public ClaimProps getClaimProps() {
    return new ClaimProps();
  }

  private KeyStore getKeyStore(String base64, String password)
      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    String noSpaceBase64 = base64.replaceAll("\\s+", "");
    byte[] decodedBytes = new byte[] {};
    try {
      decodedBytes = Base64.getDecoder().decode(noSpaceBase64);
    } catch (IllegalArgumentException e) {
      decodedBytes = noSpaceBase64.getBytes();
    }
    InputStream stream = new ByteArrayInputStream(decodedBytes);
    keyStore.load(stream, password.toCharArray());
    stream.close();
    return keyStore;
  }

  /**
   * Get Rest template for BIP API connection.
   *
   * @param builder RestTemplateBuilder
   * @return Rest template, request factory
   * @throws BipException failure to create connection
   */
  @Bean(name = "bipCERestTemplate")
  public RestTemplate getHttpsRestTemplate(RestTemplateBuilder builder) throws BipException {
    try {
      if (trustStore.isEmpty() && password.isEmpty()) { // skip if it is test.
        log.info("No valid BIP mTLS setup. Skip related setup.");
        return new RestTemplate();
      }

      log.info("-------load keystore");
      KeyStore keyStoreObj = getKeyStore(keystore, password);
      log.info("-------load truststore");
      KeyStore trustStoreObj = getKeyStore(trustStore, password);

      log.info("-------build SSLContext");
      KeyManagerFactory keyManagerFactory =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStoreObj, password.toCharArray());

      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(trustStoreObj);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(
          keyManagerFactory.getKeyManagers(),
          trustManagerFactory.getTrustManagers(),
          new SecureRandom());

      SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

      HttpClientConnectionManager connectionManager =
          PoolingHttpClientConnectionManagerBuilder.create()
              .setSSLSocketFactory(sslConFactory)
              .build();
      CloseableHttpClient httpClient =
          HttpClients.custom().setConnectionManager(connectionManager).build();
      ClientHttpRequestFactory requestFactory =
          new HttpComponentsClientHttpRequestFactory(httpClient);
      return new RestTemplate(requestFactory);
    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
      log.error("Failed to create SSL context for VA certificate. {}", e.getMessage(), e);
      throw new BipException("Failed to create SSL context.", e);
    } catch (Exception e) {
      log.error("Unexpected error.", e);
      throw new BipException(e.getMessage(), e);
    }
  }
}
