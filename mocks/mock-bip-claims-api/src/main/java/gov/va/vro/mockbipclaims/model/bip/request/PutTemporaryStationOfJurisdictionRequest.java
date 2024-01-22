package gov.va.vro.mockbipclaims.model.bip.request;

import gov.va.vro.mockbipclaims.model.bip.ProviderRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PutTemporaryStationOfJurisdictionRequest extends ProviderRequest {

  @NotNull private String tempStationOfJurisdiction;
}
