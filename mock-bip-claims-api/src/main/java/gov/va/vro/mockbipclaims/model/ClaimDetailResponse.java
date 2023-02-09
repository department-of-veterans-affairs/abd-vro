package gov.va.vro.mockbipclaims.model;

import lombok.Data;

import java.util.List;
import javax.validation.Valid;

/** ClaimDetailResponse. */
@Data
public class ClaimDetailResponse {
  @Valid private List<Message> messages = null;

  private ClaimDetail claim;
}
