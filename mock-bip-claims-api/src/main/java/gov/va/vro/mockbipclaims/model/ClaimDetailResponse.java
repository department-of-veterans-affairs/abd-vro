package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ClaimDetailResponse. */
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimDetailResponse {

  @JsonProperty("messages")
  @Valid
  private List<Message> messages = null;

  @JsonProperty("claim")
  private ClaimDetail claim;

  public ClaimDetailResponse messages(List<Message> messages) {
    this.messages = messages;
    return this;
  }

  /**
   * Adds a message.
   *
   * @param messagesItem message
   * @return claim detail
   */
  public ClaimDetailResponse addMessagesItem(Message messagesItem) {
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

  public ClaimDetailResponse claim(ClaimDetail claim) {
    this.claim = claim;
    return this;
  }

  /**
   * Get claim.
   *
   * @return claim
   */
  @Valid
  @Schema(name = "claim")
  public ClaimDetail getClaim() {
    return claim;
  }

  public void setClaim(ClaimDetail claim) {
    this.claim = claim;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimDetailResponse claimDetailResponse = (ClaimDetailResponse) o;
    return Objects.equals(this.messages, claimDetailResponse.messages)
        && Objects.equals(this.claim, claimDetailResponse.claim);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages, claim);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimDetailResponse {\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
    sb.append("    claim: ").append(toIndentedString(claim)).append("\n");
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
