package gov.va.starter.example.service.spi.db.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Veteran {

  @NotNull private String icn;
  private String participantId;
}
