package gov.va.vro.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Configuration
public class ServiceProviderConfig {

  // Default to false for unit tests
  @Value("${vro.persist.tracking.enabled:false}")
  public boolean persistTrackingEnabled;

  @Value("${vro.persist.tracking.base_folder:/tmp/vro-test-persist/}")
  @NotNull
  @NotBlank
  public String baseTrackingFolder;
}
