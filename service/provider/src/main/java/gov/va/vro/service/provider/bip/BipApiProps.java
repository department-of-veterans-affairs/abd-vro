package gov.va.vro.service.provider.bip;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties used in BIP API service.
 *
 * @author warren @Date 10/31/22
 */
@Getter
@Setter
public class BipApiProps {

  private String baseURL;

  private String secret;

  private String clientId;
}
