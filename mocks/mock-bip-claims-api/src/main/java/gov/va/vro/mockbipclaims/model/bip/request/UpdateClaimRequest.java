package gov.va.vro.mockbipclaims.model.bip.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/** UpdateClaimRequest. */
@Data
public class UpdateClaimRequest {
  private String suspenseReasonCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime suspenseDate;

  private String commentText;
}
