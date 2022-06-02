package gov.va.vro.service.spi.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString(includeFieldNames = true)
public class AssessHealthData {

  @NonNull private String contention;

  // input
  private String bpObservations;

  // output
  private String bpReadingsJson;
}
