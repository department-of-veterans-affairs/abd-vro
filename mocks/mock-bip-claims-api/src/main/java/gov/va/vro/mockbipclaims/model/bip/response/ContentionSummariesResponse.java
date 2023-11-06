package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.Message;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/** ContentionSummariesResponse. */
@Data
public class ContentionSummariesResponse {

  @Valid private List<Message> messages = null;

  @Valid private List<ContentionSummary> contentions = null;
}
