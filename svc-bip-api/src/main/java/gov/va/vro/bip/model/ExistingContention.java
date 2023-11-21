package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/** Data object used in update claim contention(s) endpoint: PUT /claims/{claimId}/contentions */
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExistingContention extends Contention {
  @JsonProperty("contentionId")
  private long contentionId;

  @JsonProperty("lastModified")
  private String lastModified;

  @JsonProperty("lifecycleStatus")
  private String lifecycleStatus;

  @JsonProperty("action")
  private String action;

  @JsonProperty("automationIndicator")
  private boolean automationIndicator;
}
