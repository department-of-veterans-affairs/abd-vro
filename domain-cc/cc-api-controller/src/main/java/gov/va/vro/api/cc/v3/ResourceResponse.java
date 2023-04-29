package gov.va.vro.api.cc.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceResponse {

  private int statusCode;
  private String responseBody;
}
