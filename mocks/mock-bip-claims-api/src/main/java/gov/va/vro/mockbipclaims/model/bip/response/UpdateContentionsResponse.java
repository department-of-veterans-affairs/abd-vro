package gov.va.vro.mockbipclaims.model.bip.response;

import gov.va.vro.mockbipclaims.model.bip.Message;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** UpdateContentionsResponse. */
@Data
public class UpdateContentionsResponse {
  @Valid private List<Message> messages = null;

  /**
   * Adds a new message.
   *
   * @param message message to be added
   */
  public void addMessagesItem(Message message) {
    if (messages == null) {
      messages = new ArrayList<>();
    }
    messages.add(message);
  }
}
