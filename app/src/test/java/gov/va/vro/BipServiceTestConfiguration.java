package gov.va.vro;

import gov.va.vro.service.MockBipApiService;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * BIP test configuration.
 *
 * @author warren @Date 2/10/23
 */
@TestConfiguration
public class BipServiceTestConfiguration {

  @Bean
  @Primary
  public IBipApiService getBipApiService() {
    return new MockBipApiService();
  }
}
