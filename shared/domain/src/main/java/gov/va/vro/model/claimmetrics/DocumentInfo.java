package gov.va.vro.model.claimmetrics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class DocumentInfo {
  private String documentName;

  private Map<String, String> evidenceInfo;
}
