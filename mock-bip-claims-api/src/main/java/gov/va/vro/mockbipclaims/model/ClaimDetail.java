package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/**
 * An object to provide more detailed data for a specific claim. A claim is a formal request for
 * benefits that is submitted by a Veteran, or by a person or organization authorized to act on a
 * Veteran&#39;s behalf.
 */
@Schema(
    name = "ClaimDetail",
    description =
        """
        An object to provide more detailed data for a specific claim. A claim is a formal
        request for benefits that is submitted by a Veteran, or by a person or organization
        authorized to act on a Veteran's behalf.
        """)
@Data
public class ClaimDetail {
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

  private String tempStationOfJurisdiction;

  private String claimStationOfJurisdiction;

  private String awardStationOfJurisdiction;

  private Suspense suspense;

  private String suspenseReasonCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime suspenseDate;

  private String suspenseComment;

  @JsonProperty("waiverSubmitted")
  private Boolean waiverSubmitted;

  private LimitedPoa limitedPoa;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime establishedDate;
}
