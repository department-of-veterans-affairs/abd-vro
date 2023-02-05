package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * A person making a claim, which could be the Veteran, one of their dependents, or an organization
 * acting on behalf of the Veteran.
 */
@Schema(
    name = "Claimant",
    description =
        """
        A person making a claim, which could be the Veteran, one of their dependents, or an
        organization acting on behalf of the Veteran.
        """)
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Claimant {

  @JsonProperty("participantId")
  private Long participantId;

  public Claimant participantId(Long participantId) {
    this.participantId = participantId;
    return this;
  }

  /**
   * The CorpDB Participant ID. The Claims API assumes the caller will have obtained this value from
   * MVI. maximum: 999999999999999.
   *
   * @return participantId
   */
  @NotNull
  @Max(999999999999999L)
  @Schema(
      name = "participantId",
      example = "307405",
      description =
          """
          The CorpDB Participant ID. The Claims API assumes the caller will have obtained
          this value from MVI.
          """)
  public Long getParticipantId() {
    return participantId;
  }

  public void setParticipantId(Long participantId) {
    this.participantId = participantId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Claimant claimant = (Claimant) o;
    return Objects.equals(this.participantId, claimant.participantId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(participantId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Claimant {\n");
    sb.append("    participantId: ").append(toIndentedString(participantId)).append("\n");
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
