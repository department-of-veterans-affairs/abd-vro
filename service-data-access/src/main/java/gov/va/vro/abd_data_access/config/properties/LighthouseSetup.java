package gov.va.vro.abd_data_access.config.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Lighthouse FHIR API access setup data.
 *
 * @author Warren Lin
 */
@Getter
@Setter
public class LighthouseSetup {
  private String clientId;
  private String assertionurl;
  private String tokenurl;
  private String fhirurl;
}
