package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** The association between a contention and tracked item. */
@Schema(
    name = "ContentionTrackedItemAssociation",
    description = "The association between a contention and tracked item.")
@Data
public class ContentionTrackedItemAssociation {
  private Long contentionId;

  private Long trackedItemId;
}
