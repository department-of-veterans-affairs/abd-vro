package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
public class VeteranEntity extends BaseEntity {

  // (unique): Internal Control Number; needed for queries to Lighthouse Health API
  @NotNull private String icn;
  // common identifier used by BGS
  private String participantId;
}
