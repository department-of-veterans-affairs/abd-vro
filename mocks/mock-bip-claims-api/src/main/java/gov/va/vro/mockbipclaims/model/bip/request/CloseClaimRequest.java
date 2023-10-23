package gov.va.vro.mockbipclaims.model.bip.request;

import gov.va.vro.mockbipclaims.model.bip.ProviderRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CloseClaimRequest extends ProviderRequest {

  @NotNull private String lifecycleStatusReasonCode;
  private String closeReasonText;
}
