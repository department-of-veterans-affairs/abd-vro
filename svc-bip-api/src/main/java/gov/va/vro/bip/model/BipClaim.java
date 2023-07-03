package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.xample.SomeDtoModelHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipClaim {
  //todo: the dependency on example code has to be removed
  @Builder.Default private SomeDtoModelHeader header = new SomeDtoModelHeader();
  public BipClaim(int httpStatus, String httpMessage){
    super();
    header.setStatusCode(httpStatus);
    header.setStatusMessage(httpMessage);
  }
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
