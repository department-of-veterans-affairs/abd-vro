package gov.va.vro.mockbipclaims.model;

import lombok.Data;

import java.util.List;
import javax.validation.Valid;

/** UpdateClaimResponse. */
@Data
public class UpdateClaimResponse {
  @Valid private List<Message> messages = null;
}
