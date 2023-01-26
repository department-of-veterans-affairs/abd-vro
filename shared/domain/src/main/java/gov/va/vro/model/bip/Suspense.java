package gov.va.vro.model.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Suspense class.
 *
 * @author warren @Date 11/9/22
 */
@RequiredArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Suspense {
  private String reason;
  private String reasonCode;
  private String date;
  private String comment;
  private String changedDate;
  private String changedBy;
}
