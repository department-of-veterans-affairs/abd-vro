package gov.va.vro.model.bipevidence.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.UUID;
import javax.validation.Valid;

/** The root schema comprises generic File Store Errors. */
@Schema(
    name = "vefsErrorResponse",
    description = "The root schema comprises generic File Store Errors.")
@JsonTypeName("vefsErrorResponse")
public class VefsErrorResponse {

  @JsonProperty("uuid")
  private UUID uuid;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  public VefsErrorResponse uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * UUID used to trace the error response.
   *
   * @return uuid
   */
  @Valid
  @Schema(name = "uuid", description = "UUID used to trace the error response", required = false)
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public VefsErrorResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Enumerated Error Code indicating the error classification.
   *
   * @return code
   */
  @Schema(
      name = "code",
      description = "Enumerated Error Code indicating the error classification.",
      required = false)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public VefsErrorResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * The message describing the error.
   *
   * @return message
   */
  @Schema(name = "message", description = "The message describing the error.", required = false)
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VefsErrorResponse vefsErrorResponse = (VefsErrorResponse) o;
    return Objects.equals(this.uuid, vefsErrorResponse.uuid)
        && Objects.equals(this.code, vefsErrorResponse.code)
        && Objects.equals(this.message, vefsErrorResponse.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, code, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VefsErrorResponse {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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
