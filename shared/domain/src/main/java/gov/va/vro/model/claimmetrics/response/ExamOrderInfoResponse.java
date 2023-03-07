package gov.va.vro.model.claimmetrics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ExamOrderInfoResponse {
    private String collectionId;
    private String status;
    private OffsetDateTime orderedAt;
    private boolean hasAssociatedClaimSubmission;
}
