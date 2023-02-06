package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ContentionSummariesResponse. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionSummariesResponse {

  @JsonProperty("messages")
  @Valid
  private List<Message> messages = null;

  @JsonProperty("contentions")
  @Valid
  private List<ContentionSummary> contentions = null;

  public ContentionSummariesResponse messages(List<Message> messages) {
    this.messages = messages;
    return this;
  }

  /**
   * Add messages item.
   *
   * @param messagesItem messages item
   * @return provider response
   */
  public ContentionSummariesResponse addMessagesItem(Message messagesItem) {
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

  /**
   * Set messages.
   *
   * @param messages input
   */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  /**
   * Contentions.
   *
   * @param contentions Contentions
   * @return Reponse
   */
  public ContentionSummariesResponse contentions(List<ContentionSummary> contentions) {
    this.contentions = contentions;
    return this;
  }

  /**
   * Contentions item.
   *
   * @param contentionsItem contetion item
   * @return response
   */
  public ContentionSummariesResponse addContentionsItem(ContentionSummary contentionsItem) {
    if (this.contentions == null) {
      this.contentions = new ArrayList<>();
    }
    this.contentions.add(contentionsItem);
    return this;
  }

  /**
   * Get contentions.
   *
   * @return contentions
   */
  @Valid
  @Schema(name = "contentions")
  public List<ContentionSummary> getContentions() {
    return contentions;
  }

  public void setContentions(List<ContentionSummary> contentions) {
    this.contentions = contentions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionSummariesResponse contentionSummariesResponse = (ContentionSummariesResponse) o;
    return Objects.equals(this.messages, contentionSummariesResponse.messages)
        && Objects.equals(this.contentions, contentionSummariesResponse.contentions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages, contentions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionSummariesResponse {\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
    sb.append("    contentions: ").append(toIndentedString(contentions)).append("\n");
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
