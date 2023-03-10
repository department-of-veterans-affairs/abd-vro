package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.Message;
import lombok.Data;

import java.util.List;
import javax.validation.Valid;

/** ClaimDetailResponse. */
@Data
public class ClaimDetailResponse {
  @Valid private List<Message> messages = null;

  private ClaimDetail claim;
}
