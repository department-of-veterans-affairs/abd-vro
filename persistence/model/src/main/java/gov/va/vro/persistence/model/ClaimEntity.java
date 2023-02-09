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

  private String collectionId;

  @NotNull private String vbmsId;

  private String offRampReason;

  private boolean presumptiveFlag;

  private String disabilityActionType;

  private boolean inScope;

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
