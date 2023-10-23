package gov.va.vro.persistence.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "veteran")
public class VeteranEntity extends BaseEntity {

  // (unique): Internal Control Number; needed for queries to Lighthouse Health API
  @NotNull private String icn;
  // common identifier used by BGS
  private String participantId;

  private Date icnTimestamp = new Date();

  @OneToMany(mappedBy = "veteran", cascade = CascadeType.ALL)
  private List<VeteranFlashIdEntity> flashIds = new ArrayList<>();
}
