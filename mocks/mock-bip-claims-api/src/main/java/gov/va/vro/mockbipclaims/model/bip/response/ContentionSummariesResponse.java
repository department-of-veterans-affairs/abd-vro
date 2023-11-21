package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/** ContentionSummariesResponse. */
@Data
public class ContentionSummariesResponse extends ProviderResponse {
  @Valid private List<ContentionSummary> contentions = null;
}
