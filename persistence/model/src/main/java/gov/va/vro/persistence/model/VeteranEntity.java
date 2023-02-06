package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "veteran")
public class VeteranEntity extends BaseEntity {

  // (unique): Internal Control Number; needed for queries to Lighthouse Health API
  @NotNull private String icn;
  // common identifier used by BGS
  private String participantId;

  private String icnTimestamp;

  @OneToMany(
      mappedBy = "veteran",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<VeteranFlashIdEntity> flashIds = new ArrayList<>();
}
