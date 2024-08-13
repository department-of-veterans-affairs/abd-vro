package gov.va.vro.bip.model.contentions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class SpecialIssueType {
  @JsonProperty("name")
  private String name;

  @JsonProperty("code")
  private final String code;

  @JsonProperty("description")
  private String description;

  @JsonProperty("deactiveDate")
  private OffsetDateTime deactiveDate;
}
