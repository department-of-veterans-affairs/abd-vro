package gov.va.vro.model.xample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
// Use toBuilder as a copy constructor
@Builder(toBuilder = true)
@Data
public class SomeDtoModelHeader {
  // statusCode and statusMessage are for interfacing with internal microservices
  // They could be moved to a wrapper class to keep them separate from the domain model.

  // statusCode should correspond to https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
  @Builder.Default private int statusCode = 0;
  // Message to go with the statusCode
  private String statusMessage;
}
