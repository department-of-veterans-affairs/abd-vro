package gov.va.vro.mockshared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Base64;

@RequiredArgsConstructor
public class KeystoreSpec {
  private final String keyStoreBase64;

  @Getter private final String keyStorePassword;

  private final String trustStoreBase64;
  private final String trustStorePassword;

  @SneakyThrows
  private static KeyStore getKeyStore(String base64, String password) {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    String noSpaceBase64 = base64.replaceAll("\\s+", "");
    byte[] decodedBytes = Base64.getDecoder().decode(noSpaceBase64);
    InputStream stream = new ByteArrayInputStream(decodedBytes);
    keyStore.load(stream, password.toCharArray());
    return keyStore;
  }

  public KeyStore getKeystore() {
    return getKeyStore(keyStoreBase64, keyStorePassword);
  }

  public KeyStore getTrustStore() {
    return getKeyStore(trustStoreBase64, trustStorePassword);
  }
}
