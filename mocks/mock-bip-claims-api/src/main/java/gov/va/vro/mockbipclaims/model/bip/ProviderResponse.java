package gov.va.vro.mockbipclaims.model.bip;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

/** ProviderResponse. */
@JsonTypeName("provider_response")
@Data
public class ProviderResponse {
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
