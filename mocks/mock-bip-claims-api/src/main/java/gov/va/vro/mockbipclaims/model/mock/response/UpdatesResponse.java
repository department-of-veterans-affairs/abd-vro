package gov.va.vro.mockbipclaims.model.mock.response;

import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdatesResponse {
  private boolean found;
  private List<ContentionSummary> contentions = null;

  public UpdatesResponse(boolean found) {
    this.found = found;
  }
}
