package gov.va.vro.service.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties used in BIP API service.
 *
 * @author warren @Date 10/31/22
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bip")
public class BipApiProps {

  private String claimBaseUrl;

  private String claimSecret;

  private String claimClientId;

  private String claimIssuer;

  private String evidenceBaseUrl;

  private String evidenceSecret;

  private String evidenceClientId;

  private String evidenceIssuer;

  private String stationId;

  private String jti;

  private String applicationId;

  private String applicationName;
}
