package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Limited Power of Attorney (POA) representation. A Veteran Service Organization (VSO) or other
 * individual appointed by the Veteran or claimant who represents their interests.Limited
 * representation is only for a specific claim, versus a general POA that can be associated with all
 * claims for a claimant.
 */
@Schema(
    name = "LimitedPoa",
    description =
        """
        Limited Power of Attorney (POA) representation. A Veteran Service Organization (VSO)
        or other individual appointed by the Veteran or claimant who represents their
        interests.Limited representation is only for a specific claim, versus a general POA
        that can be associated with all claims for a claimant.
        """)
@Data
public class LimitedPoa {
  private Long veteranParticipantId;

  private Long claimantParticipantId;

  private Long poaParticipantId;

  private String poaCode;
}
