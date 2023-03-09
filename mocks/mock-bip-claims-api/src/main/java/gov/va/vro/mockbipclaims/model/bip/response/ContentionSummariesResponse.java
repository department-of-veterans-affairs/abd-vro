package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.Message;
import lombok.Data;

import java.util.List;
import javax.validation.Valid;

/** ContentionSummariesResponse. */
@Data
public class ContentionSummariesResponse {

  @Valid private List<Message> messages = null;

  @Valid private List<ContentionSummary> contentions = null;
}
