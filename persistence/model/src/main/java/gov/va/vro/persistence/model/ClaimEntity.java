package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Getter
@Setter
public class ClaimEntity extends BaseEntity {

  // claim identifier used by client
  private String claimId;

  // omain of the id, e.g. "va.gov-Form526Submission"
  private String idType;

  private String incomingStatus = "submission";

  @ManyToOne private VeteranEntity veteran;

  @OneToMany(
      mappedBy = "claim",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ContentionEntity> contentions = new ArrayList<>();

  public void addContention(ContentionEntity contention) {
    contention.setClaim(this);
    contentions.add(contention);
  }
}
