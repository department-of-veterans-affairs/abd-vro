package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "claim")
public class ClaimEntity extends BaseEntity {

  @NotNull private String vbmsId;

  private boolean presumptiveFlag;

  private boolean rfdFlag;

  private String disabilityActionType;

  @ManyToOne private VeteranEntity veteran;

  @OneToMany(
      mappedBy = "claim",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ContentionEntity> contentions = new ArrayList<>();

  @OneToMany(
      mappedBy = "claim",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ClaimSubmissionEntity> claimSubmissions = new HashSet<>();

  public void addContention(ContentionEntity contention) {
    contention.setClaim(this);
    contentions.add(contention);
  }

  public void addClaimSubmission(ClaimSubmissionEntity claimSubmission) {
    claimSubmission.setClaim(this);
    claimSubmissions.add(claimSubmission);
  }
}
