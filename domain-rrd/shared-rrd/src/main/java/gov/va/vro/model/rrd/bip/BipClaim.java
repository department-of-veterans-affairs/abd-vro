package gov.va.vro.model.rrd.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * BIP claim.
 *
 * @author warren @Date 11/8/22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipClaim {
  private String summaryDateTime;
  private String lastModified;
  private String claimId;
  private BenefitClaimType benefitClaimType;
  private String phase;
  private String phaseLastChangedDate;
  private String claimLifecycleStatus;
  private String completedDate;
  private Claimant claimant;
  private Veteran veteran;
  private String receivedDate;
  private String payeeTypeCode;
  private String serviceTypeCode;
  private String programTypeCode;
  private String endProductCode;
  private String tempStationOfJurisdiction;
  private String claimStationOfJurisdiction;
  private String awardStationOfJurisdiction;
  private Suspense suspense;
  private String suspenseReasonCode;
  private String suspenseDate;
  private String suspenseComment;
  private String establishedDate;
}
