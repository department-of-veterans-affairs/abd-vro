package gov.va.vro.mockbipclaims.model.mock.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LifecycleUpdatesResponse {
  private boolean found;
  private String status;

  public LifecycleUpdatesResponse(boolean found) {
    this.found = found;
  }
}
