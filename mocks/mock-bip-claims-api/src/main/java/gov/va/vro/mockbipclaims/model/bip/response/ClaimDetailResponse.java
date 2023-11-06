package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import lombok.Data;

/** ClaimDetailResponse. */
@Data
public class ClaimDetailResponse extends ProviderResponse {
  private ClaimDetail claim;
}
