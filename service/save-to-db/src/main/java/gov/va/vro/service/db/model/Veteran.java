package gov.va.vro.service.db.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Veteran {

  @NotNull private String icn;
  private String participantId;
}
