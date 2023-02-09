package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** Holds name and boolean status of a feature flag, as found in bip-vetservices-claims.yml. */
@Schema(
    name = "FeatureFlag",
    description =
        "Holds name and boolean status of a feature flag, as found in bip-vetservices-claims.yml")
@Data
public class FeatureFlag {
  private String featureFlagName;

  private Boolean featureFlagStatus;
}
