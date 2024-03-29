package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * A person making a claim, which could be the Veteran, one of their dependents, or an organization
 * acting on behalf of the Veteran.
 */
@Data
@Schema(
    name = "Claimant",
    description =
        """
        A person making a claim, which could be the Veteran, one of their dependents, or an
        organization acting on behalf of the Veteran.
        """)
public class Claimant {
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
  private Long participantId;
}
