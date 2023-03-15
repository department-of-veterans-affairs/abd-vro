package gov.va.vro.mockbipclaims.model.mock.response;

import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContentionUpdatesResponse {
  private boolean found;
  private List<ContentionSummary> contentions = null;

  public ContentionUpdatesResponse(boolean found) {
    this.found = found;
  }
}
