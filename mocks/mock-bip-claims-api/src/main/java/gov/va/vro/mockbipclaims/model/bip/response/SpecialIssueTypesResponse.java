package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import gov.va.vro.mockbipclaims.model.bip.SpecialIssueType;
import lombok.Data;

/** SpecialIssueTypesResponse. */
@Data
public class SpecialIssueTypesResponse extends ProviderResponse {
  private SpecialIssueType[] codeNamePairs;
}
