package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "claim_submission")
public class ClaimSubmissionEntity extends BaseEntity {

  @NotNull String referenceId;

  // domain of the id, e.g. "va.gov-Form526Submission"
  @NotNull private String idType;

  private String incomingStatus;

  private String submissionSource;

  private OffsetDateTime submissionDate;

  @OneToMany(
          mappedBy = "claimSubmission",
          fetch = FetchType.EAGER,
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  private List<ContentionEntity> contentions = new ArrayList<>();

  public void addContention(ContentionEntity contention) {
    contention.setClaimSubmission(this);
    contentions.add(contention);
  }
}