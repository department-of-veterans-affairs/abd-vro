package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ClaimLifecycleStatusesResponse. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimLifecycleStatusesResponse {

  @JsonProperty("messages")
  @Valid
  private List<Message> messages = null;

  @JsonProperty("lifecycleStatuses")
  @Valid
  private List<Lifecycle> lifecycleStatuses = null;

  public ClaimLifecycleStatusesResponse messages(List<Message> messages) {
    this.messages = messages;
    return this;
  }

  /**
   * Adds a message.
   *
   * @param messagesItem message content
   * @return response
   */
  public ClaimLifecycleStatusesResponse addMessagesItem(Message messagesItem) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    this.messages.add(messagesItem);
    return this;
  }

  /**
   * Get messages.
   *
   * @return messages
   */
  @Valid
  @Schema(name = "messages")
  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  /**
   * Adds lifesycle statuses.
   *
   * @param lifecycleStatuses statuses
   * @return response
   */
  public ClaimLifecycleStatusesResponse lifecycleStatuses(List<Lifecycle> lifecycleStatuses) {
    this.lifecycleStatuses = lifecycleStatuses;
    return this;
  }

  /**
   * Adds a lifecyle status item.
   *
   * @param lifecycleStatusesItem item
   * @return Claim lifecycle
   */
  public ClaimLifecycleStatusesResponse addLifecycleStatusesItem(Lifecycle lifecycleStatusesItem) {
    if (this.lifecycleStatuses == null) {
      this.lifecycleStatuses = new ArrayList<>();
    }
    this.lifecycleStatuses.add(lifecycleStatusesItem);
    return this;
  }

  /**
   * Get lifecycleStatuses.
   *
   * @return lifecycleStatuses
   */
  @Valid
  @Schema(name = "lifecycleStatuses")
  public List<Lifecycle> getLifecycleStatuses() {
    return lifecycleStatuses;
  }

  public void setLifecycleStatuses(List<Lifecycle> lifecycleStatuses) {
    this.lifecycleStatuses = lifecycleStatuses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimLifecycleStatusesResponse claimLifecycleStatusesResponse =
        (ClaimLifecycleStatusesResponse) o;
    return Objects.equals(this.messages, claimLifecycleStatusesResponse.messages)
        && Objects.equals(this.lifecycleStatuses, claimLifecycleStatusesResponse.lifecycleStatuses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages, lifecycleStatuses);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimLifecycleStatusesResponse {\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
    sb.append("    lifecycleStatuses: ").append(toIndentedString(lifecycleStatuses)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
