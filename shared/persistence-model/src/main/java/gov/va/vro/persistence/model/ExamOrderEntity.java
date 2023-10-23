package gov.va.vro.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "exam_order")
public class ExamOrderEntity extends BaseEntity {

  private String collectionId;

  private String status;

  private OffsetDateTime orderedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "claim_submission_id")
  private ClaimSubmissionEntity claimSubmission;
}
