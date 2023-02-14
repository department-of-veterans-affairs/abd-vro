package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Getter
@Setter
@Table(name = "exam_order")
public class ExamOrderEntity extends BaseEntity {

  private String collectionId;

  private String status;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "claim_submission_id")
  private ClaimSubmissionEntity claimSubmission;
}
