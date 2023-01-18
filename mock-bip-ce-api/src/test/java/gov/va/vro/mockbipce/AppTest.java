package gov.va.vro.mockbipce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.model.bip.BipFileProviderData;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppTest {
  @Value("${http.client.ssl.trust-store}")
  private Resource trustStore;

  @Value("${http.client.ssl.trust-store-password}")
  private String trustStorePassword;

  @Value("${http.client.ssl.key-store}")
  private Resource keyStoreResource;

  @Value("${http.client.ssl.key-store-password}")
  private String keyStorePassword;

  @LocalServerPort int port;

  static private RestTemplate restTemplate;

  @BeforeEach
  void init()
      throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());
    SSLContext sslContext = new SSLContextBuilder()
        .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
        .build();

    SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    restTemplate = new RestTemplate(requestFactory);
  }

  @Test
  void postFileTest() {
    BipFileProviderData request = BipFileProviderData.builder()
        .claimantLastName("Doe").claimantFirstName("Joe").build();

    ResponseEntity<BipCeFileUploadResponse> response = restTemplate.postForEntity(
      "https://localhost:" + port + "/mock-bip-ce/files", request, BipCeFileUploadResponse.class
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
