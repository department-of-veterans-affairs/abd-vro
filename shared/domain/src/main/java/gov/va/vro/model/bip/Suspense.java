package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** @author warren @Date 11/9/22 */
@Component
@RequiredArgsConstructor
@Data
public class Suspense {
  private String reason;
  private String reasonCode;
  private String date;
  private String comment;
  private String changedDate;
  private String changedBy;
}
