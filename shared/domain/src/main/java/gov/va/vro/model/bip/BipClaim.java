package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** @author warren @Date 11/8/22 */
@Component
@RequiredArgsConstructor
@Data
public class BipClaim {
  private String summaryDateTime;
  private String lastModified;
  private String claimId;
  private BenefitClaimType benefitClaimType;
  private String phase;
  private String phaseLastChangedDate;
  private String claimLifecycleStatus;
  private Claimant claimant;
  private Veteran veteran;
  private String receivedDate;
  private String payeeTypeCode;
  private String serviceTypeCode;
  private String programTypeCode;
  private String tempStationOfJurisdiction;
  private String claimStationOfJurisdiction;
  private String awardStationOfJurisdiction;
  private Suspense suspense;
  private String suspenseReasonCode;
  private String suspenseDate;
  private String suspenseComment;
  private String establishedDate;
}
