package gov.va.vro.mockbipclaims.model;

import lombok.Data;

import java.util.List;
import javax.validation.Valid;

/** ClaimLifecycleStatusesResponse. */
@Data
public class ClaimLifecycleStatusesResponse {

  @Valid private List<Message> messages = null;

  @Valid private List<Lifecycle> lifecycleStatuses = null;
}
