package gov.va.vro.service.spi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VeteranInfo {
  private String first;
  private String middle;
  private String last;
  private String suffix;
  private String birthdate;
}
