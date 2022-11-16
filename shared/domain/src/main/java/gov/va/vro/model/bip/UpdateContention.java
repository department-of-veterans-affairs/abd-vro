package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Claim contention object is used in BIP contention API.
 *
 * @author warren @Date 11/9/22
 */
@RequiredArgsConstructor
@Getter
@Setter
public class UpdateContention {
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
  private long contentionId;
  private String lastModified;
}
