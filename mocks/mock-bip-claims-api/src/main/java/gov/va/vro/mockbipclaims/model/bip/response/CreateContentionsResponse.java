package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateContentionsResponse extends ProviderResponse {
  @Valid private List<Long> contentionIds = null;

  public void addContentionId(long contentionId) {
    if (contentionIds == null) {
      contentionIds = new ArrayList<>();
    }
    contentionIds.add(contentionId);
  }
}
