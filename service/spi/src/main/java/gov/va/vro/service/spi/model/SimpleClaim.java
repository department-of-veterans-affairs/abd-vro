package gov.va.vro.service.spi.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class SimpleClaim {
  public String claimSubmissionId;
  public String veteranIcn;
  List<String> contentions;
}
