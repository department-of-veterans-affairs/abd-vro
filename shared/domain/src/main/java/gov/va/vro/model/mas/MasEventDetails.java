package gov.va.vro.model.mas;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MasEventDetails {

  private String claimId;
  private String collectionId;
  private String veteranIcn;
  private String diagnosticCode;
  private String offRampReason;
  private String disabilityActionType;
  private Boolean presumptive;
  private boolean inScope;
  private List<String> flashIds;
  private String submissionDate;
  private String submissionSource;
}
