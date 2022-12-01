package gov.va.vro.model.bip;

import lombok.*;

import java.util.List;

/**
 * Claim contention object is used in BIP contention API.
 *
 * @author warren @Date 11/9/22
 */
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClaimContention {
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
  private String summaryDateTime;
}
