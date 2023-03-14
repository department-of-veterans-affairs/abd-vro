package gov.va.vro.end2end.util;

import gov.va.vro.model.bip.ClaimContention;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContentionUpdatesResponse {
  private boolean found;
  private List<ClaimContention> contentions;
}
