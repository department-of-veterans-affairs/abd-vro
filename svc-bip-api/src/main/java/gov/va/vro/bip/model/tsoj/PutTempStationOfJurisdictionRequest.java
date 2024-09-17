package gov.va.vro.bip.model.tsoj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.bip.model.BipPayloadRequest;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PutTempStationOfJurisdictionRequest extends BipPayloadRequest {
  @JsonProperty("claimId")
  private long claimId;

  @JsonProperty("tempStationOfJurisdiction")
  private String tempStationOfJurisdiction;
}
