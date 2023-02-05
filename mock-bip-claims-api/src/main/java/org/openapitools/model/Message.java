package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/** Model that identifies a single individual used in the security context. */
@Schema(
    name = "message",
    description = "Model that identifies a single individual used in the security context")
@JsonTypeName("message")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Message {

  @JsonProperty("key")
  private String key;

  @JsonProperty("severity")
  private String severity;

  @JsonProperty("status")
  private Integer status;

  @JsonProperty("text")
  private String text;

  @JsonProperty("timestamp")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;

  public Message key(String key) {
    this.key = key;
    return this;
  }

  /**
   * Get key.
   *
   * @return key
   */
  @NotNull
  @Schema(name = "key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Message severity(String severity) {
    this.severity = severity;
    return this;
  }

  /**
   * Get severity.
   *
   * @return severity
   */
  @NotNull
  @Schema(name = "severity")
  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public Message status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * Get status.
   *
   * @return status
   */
  @Schema(name = "status")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Message text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text.
   *
   * @return text
   */
  @Schema(name = "text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Message timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp.
   *
   * @return timestamp
   */
  @Valid
  @Schema(name = "timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(this.key, message.key)
        && Objects.equals(this.severity, message.severity)
        && Objects.equals(this.status, message.status)
        && Objects.equals(this.text, message.text)
        && Objects.equals(this.timestamp, message.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, severity, status, text, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Message {\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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
