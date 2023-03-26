package gov.va.vro.model.rrd.claimmetrics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ExamOrderInfoResponse {
  private String collectionId;
  private String status;
  private LocalDateTime orderedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean hasAssociatedClaimSubmission;
}
