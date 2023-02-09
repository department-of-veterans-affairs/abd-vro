package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "claim_submission")
public class ClaimSubmissionEntity extends BaseEntity {
  @ManyToOne private ClaimEntity claim;

  @NotNull String referenceId;

  // domain of the id, e.g. "va.gov-Form526Submission"
  @NotNull private String idType;

  private String incomingStatus;

  private String submissionSource;

  private OffsetDateTime submissionDate;
}
