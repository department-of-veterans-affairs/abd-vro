package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "claim")
public class ClaimEntity extends BaseEntity {

  private String vbmsId;

  private boolean presumptiveFlag;

  private boolean rfdFlag;

  private String disabilityActionType;

  @ManyToOne private VeteranEntity veteran;

  // Multiple collections need to be eagerly fetched without causing a cartesian join product with
  // FETCH.JOIN , or hibernates n+1 query issue by using FETCH.SELECT.
  // FETCH.SUBSELECT keeps the number of queries down (3 total), does not cause a cartesian join,
  // and permits eager fetching to still occur.

  @OneToMany(
      mappedBy = "claim",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Fetch(FetchMode.SUBSELECT)
  private List<ContentionEntity> contentions = new ArrayList<>();

  @OneToMany(
      mappedBy = "claim",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Fetch(FetchMode.SUBSELECT)
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
