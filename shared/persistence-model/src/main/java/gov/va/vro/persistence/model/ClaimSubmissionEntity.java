package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "claim_submission")
public class ClaimSubmissionEntity extends BaseEntity {
  @ManyToOne private ClaimEntity claim;

  private String referenceId;

  // domain of the id, e.g. "va.gov-Form526Submission"
  private String idType;

  private String incomingStatus;

  private String offRampReason;

  private boolean inScope;

  private String submissionSource;

  private OffsetDateTime submissionDate;
}
