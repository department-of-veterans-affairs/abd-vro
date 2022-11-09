package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** @author warren @Date 11/9/22 */
@Component
@RequiredArgsConstructor
@Data
public class ClaimContention {
  private boolean medicalInd;
  private String createDate;
  private int classificationType;
  private String claimantText;
  private String originalSourceTypeCode;
  private String lastModified;
  private String summaryDateTime;
}
