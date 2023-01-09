package gov.va.vro.persistence.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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
}
