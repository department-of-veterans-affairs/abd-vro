package gov.va.vro.model.rrd.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // BIP API can send messages
public class BipClaimResp {
  private BipClaim claim;
}
