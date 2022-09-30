package gov.va.vro.abddataaccess.config.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Lighthouse FHIR API access setup data.
 *
 * @author Warren Lin
 */
@Getter
@Setter
public class LighthouseProperties {
  private String clientId;
  private String assertionurl;
  private String tokenurl;
  private String fhirurl;
  private String pemkey;
}
