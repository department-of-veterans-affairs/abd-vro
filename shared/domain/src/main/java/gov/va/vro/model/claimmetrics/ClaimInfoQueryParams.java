package gov.va.vro.model.claimmetrics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClaimInfoQueryParams {
  @Builder.Default private int page = 0;
  @Builder.Default private int size = 10;
  private String icn;
}
