package gov.va.vro.service.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties used in BIP API service.
 *
 * @author warren @Date 10/31/22
 */
@Getter
@RequiredArgsConstructor
public class BipApiProps {

  private String claimBaseURL;

  private String claimSecret;

  private String claimClientId;

  private String evidenceBaseURL;

  private String evidenceSecret;

  private String evidenceClientId;

  private String stationID;

  private String jti;

  private String applicationId;

  private String applicationName;
}
