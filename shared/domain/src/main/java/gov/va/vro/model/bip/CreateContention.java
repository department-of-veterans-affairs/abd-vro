package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * A data object is used in a claim contention creation.
 *
 * @author warren @Date 11/14/22
 */
@RequiredArgsConstructor
@Getter
@Setter
public class CreateContention {
  private boolean medicalInd;
  private String beginDate;
  private String createDate;
  private String altContentionName;
  private String completedDate;
  private String notificationDate;
  private String contentionTypeCode;
  private int classificationType;
  private String diagnosticTypeCode;
  private String claimantText;
  private String contentionStatusTypeCode;
  private String originalSourceTypeCode;
  private List<String> specialIssueCodes;
  private TrackedItems associatedTrackedItems;
}
