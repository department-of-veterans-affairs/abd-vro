package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
