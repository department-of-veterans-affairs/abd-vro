package gov.va.vro.config.propmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Info {
  private String title = "VRO API";
  private String description = "VRO Description";
  private String version = "v1.0.25";

  private final Contact contact = new Contact();

  private final License license = new License();
}
