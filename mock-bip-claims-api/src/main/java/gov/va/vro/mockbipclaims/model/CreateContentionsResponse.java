package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** CreateContentionsResponse. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class CreateContentionsResponse {

  @JsonProperty("messages")
  @Valid
  private List<Message> messages = null;

  @JsonProperty("contentionIds")
  @Valid
  private List<Long> contentionIds = null;

  public CreateContentionsResponse messages(List<Message> messages) {
    this.messages = messages;
    return this;
  }

  /**
   * Adds a message.
   *
   * @param messagesItem message content.
   * @return response
   */
  public CreateContentionsResponse addMessagesItem(Message messagesItem) {
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

  public CreateContentionsResponse contentionIds(List<Long> contentionIds) {
    this.contentionIds = contentionIds;
    return this;
  }

  /**
   * Add contentions ids item.
   *
   * @param contentionIdsItem input
   * @return response
   */
  public CreateContentionsResponse addContentionIdsItem(Long contentionIdsItem) {
    if (this.contentionIds == null) {
      this.contentionIds = new ArrayList<>();
    }
    this.contentionIds.add(contentionIdsItem);
    return this;
  }

  /**
   * Get contentionIds.
   *
   * @return contentionIds
   */
  @Schema(name = "contentionIds")
  public List<Long> getContentionIds() {
    return contentionIds;
  }

  public void setContentionIds(List<Long> contentionIds) {
    this.contentionIds = contentionIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateContentionsResponse createContentionsResponse = (CreateContentionsResponse) o;
    return Objects.equals(this.messages, createContentionsResponse.messages)
        && Objects.equals(this.contentionIds, createContentionsResponse.contentionIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages, contentionIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateContentionsResponse {\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
    sb.append("    contentionIds: ").append(toIndentedString(contentionIds)).append("\n");
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
