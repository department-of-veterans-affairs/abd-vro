package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/** test. */
@Data
@Schema(name = "AppToken", description = "test")
public class AppToken {
  @NotNull private String userId;

  @NotNull private String userKey;

  private String applicationName;

  private String stationOfJurisdiction;

  private Boolean isExternal;
}
