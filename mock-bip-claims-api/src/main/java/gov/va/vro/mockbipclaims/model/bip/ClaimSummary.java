package gov.va.vro.mockbipclaims.model.bip;

import gov.va.vro.model.bip.BenefitClaimType;
import gov.va.vro.model.bip.Claimant;
import gov.va.vro.model.bip.Veteran;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/**
 * An object to summarize the data in a claim. A claim is a formal request for benefits that is
 * submitted by a Veteran, or by a person or organization authorized to act on a Veteran&#39;s
 * behalf.
 */
@Schema(
    name = "ClaimSummary",
    description =
        """
        An object to summarize the data in a claim. A claim is a formal request for benefits
        that is submitted by a Veteran, or by a person or organization authorized to act
        on a Veteran's behalf.
        """)
@Data
public class ClaimSummary {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime summaryDateTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastModified;

  private Long claimId;

  private BenefitClaimType benefitClaimType;

  private PhaseType phase;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime phaseLastChangedDate;

  private String claimLifecycleStatus;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  private Claimant claimant;

  private Veteran veteran;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime receivedDate;

  private String payeeTypeCode;

  private String serviceTypeCode;

  private String programTypeCode;

  private String endProductCode;
}
