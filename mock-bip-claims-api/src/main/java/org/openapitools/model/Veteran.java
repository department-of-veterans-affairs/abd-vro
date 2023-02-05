package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/** The minimal information to reference a Veteran in CorpDB. */
@Schema(name = "Veteran", description = "The minimal information to reference a Veteran in CorpDB.")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Veteran {

  @JsonProperty("participantId")
  private Long participantId;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  public Veteran participantId(Long participantId) {
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
      example = "320848",
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

  public Veteran firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName.
   *
   * @return firstName
   */
  @NotNull
  @Schema(name = "firstName", example = "RALPH")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Veteran lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName.
   *
   * @return lastName
   */
  @NotNull
  @Schema(name = "lastName", example = "HONEYMOONER")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Veteran veteran = (Veteran) o;
    return Objects.equals(this.participantId, veteran.participantId)
        && Objects.equals(this.firstName, veteran.firstName)
        && Objects.equals(this.lastName, veteran.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(participantId, firstName, lastName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Veteran {\n");
    sb.append("    participantId: ").append(toIndentedString(participantId)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
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
