package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.Message;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/** UpdateClaimResponse. */
@Data
public class UpdateClaimResponse {
  @Valid private List<Message> messages = null;
}
