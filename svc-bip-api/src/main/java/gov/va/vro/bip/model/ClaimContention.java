package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
  private String lifecycleStatus;
  private String summaryDateTime;
  private boolean automationIndicator;
  private List<TrackedItems> associatedTrackedItems;

  /**
   * Update contention.
   *
   * @param action action.
   * @return return.
   */
  public UpdateContention toUpdateContention(String action) {
    return UpdateContention.builder()
        .medicalInd(medicalInd)
        .beginDate(beginDate)
        .createDate(createDate)
        .altContentionName(altContentionName)
        .completedDate(completedDate)
        .notificationDate(notificationDate)
        .contentionTypeCode(contentionTypeCode)
        .classificationType(classificationType)
        .diagnosticTypeCode(diagnosticTypeCode)
        .claimantText(claimantText)
        .contentionStatusTypeCode(contentionStatusTypeCode)
        .originalSourceTypeCode(originalSourceTypeCode)
        .specialIssueCodes(specialIssueCodes)
        .contentionId(contentionId)
        .lastModified(lastModified)
        .lifecycleStatus(lifecycleStatus)
        .automationIndicator(automationIndicator)
        .action(action)
        .build();
  }
}
