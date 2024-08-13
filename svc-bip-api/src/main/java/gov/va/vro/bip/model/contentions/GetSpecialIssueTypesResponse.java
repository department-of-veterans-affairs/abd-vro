package gov.va.vro.bip.model.contentions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.bip.model.BipPayloadResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Supplier;

@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetSpecialIssueTypesResponse extends BipPayloadResponse
    implements Supplier<SpecialIssueType[]> {

  // contemplated using ConcurrentInitializer<SpecialIssueType[]>, but have no need for the
  // concurrency concern given present use case
  private final Supplier<SpecialIssueType[]> allTypesSupplier;

  public GetSpecialIssueTypesResponse(Supplier<SpecialIssueType[]> allTypesSupplier) {
    super(BipPayloadResponse.builder());
    this.allTypesSupplier = allTypesSupplier;
  }

  public SpecialIssueType[] get() {
    if (ArrayUtils.isEmpty(codeNamePairs)) {
      codeNamePairs = allTypesSupplier.get();
    }
    return codeNamePairs;
  }

  @JsonProperty("codeNamePairs")
  private SpecialIssueType[] codeNamePairs;
}
