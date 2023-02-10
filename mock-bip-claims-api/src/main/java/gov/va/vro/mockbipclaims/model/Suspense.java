package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/**
 * The time during which a work item or claim is inactive or waiting completion of an action from an
 * outside entity, such as waiting for evidence requested from the claimant or a third party.
 * Receipt of the evidence will lift the suspense and allow the claim to move forward through
 * processing.
 */
@Schema(
    name = "Suspense",
    description =
        """
        The time during which a work item or claim is inactive or waiting completion of an
        action from an outside entity, such as waiting for evidence requested from the claimant
        or a third party. Receipt of the evidence will lift the suspense and allow the claim to
        move forward through processing.
        """)
@Data
public class Suspense {
  private String reason;

  private String reasonCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime date;

  private String comment;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime changedDate;

  private String changedBy;
}
