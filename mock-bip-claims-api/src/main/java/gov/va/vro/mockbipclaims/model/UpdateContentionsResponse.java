package gov.va.vro.mockbipclaims.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

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
