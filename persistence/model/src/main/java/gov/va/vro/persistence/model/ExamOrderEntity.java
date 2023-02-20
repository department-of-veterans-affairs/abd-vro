package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
