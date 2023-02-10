package gov.va.vro.mockbipclaims.model;

import lombok.Data;

import java.util.List;
import javax.validation.Valid;

/** ContentionSummariesResponse. */
@Data
public class ContentionSummariesResponse {

  @Valid private List<Message> messages = null;

  @Valid private List<ContentionSummary> contentions = null;
}
