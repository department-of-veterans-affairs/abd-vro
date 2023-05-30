package gov.va.vro.propmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Info {
  private String title = "API";
  private String description = "Description";
  private String version = "v3.0.1";

  private final Contact contact = new Contact();

  private final License license = new License();
}
