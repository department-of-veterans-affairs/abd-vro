package gov.va.vro.mockbipclaims.model.bip.request;

import lombok.Data;

/** UpdateContentionLifecycleStatusRequestItem. */
@Data
public class UpdateContentionLifecycleStatusRequestItem {
  private Long contentionId;

  private String lifecycleStatus;

  private String details;
}
