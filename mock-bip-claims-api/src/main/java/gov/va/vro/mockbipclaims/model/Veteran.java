package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/** The minimal information to reference a Veteran in CorpDB. */
@Schema(name = "Veteran", description = "The minimal info to reference a Veteran in CorpDB.")
@Data
public class Veteran {
  @NotNull
  @Max(999999999999999L)
  private Long participantId;

  @NotNull private String firstName;

  @NotNull private String lastName;
}
