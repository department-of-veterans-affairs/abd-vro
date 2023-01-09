package gov.va.vro.persistence.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "claim")
public class ClaimEntity extends BaseEntity {


  // claim identifier used by client
  @NotNull private String claimSubmissionId;

  private String collectionId;

  @ManyToOne private VeteranEntity veteran;

  @OneToMany(
      mappedBy = "claim",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ContentionEntity> contentions = new ArrayList<>();

  public void addContention(ContentionEntity contention) {
    contention.setClaim(this);
    contentions.add(contention);
  }
}
