package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** The association between a contention and tracked item. */
@Schema(
    name = "TrackedItemAssociationsOnContention",
    description = "The association between a contention and tracked item.")
@Data
public class TrackedItemAssociationsOnContention {
  private Long trackedItemId;
}
