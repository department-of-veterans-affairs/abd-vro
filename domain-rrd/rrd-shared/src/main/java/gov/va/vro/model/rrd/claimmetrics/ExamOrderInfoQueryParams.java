package gov.va.vro.model.rrd.claimmetrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ExamOrderInfoQueryParams {
  @Builder.Default private int page = 0;
  @Builder.Default private int size = 10;
  @Builder.Default private Boolean notOrdered = Boolean.FALSE;
}
