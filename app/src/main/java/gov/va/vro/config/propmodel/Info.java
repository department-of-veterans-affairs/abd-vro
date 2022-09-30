package gov.va.vro.config.propmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
public class Info {
  private String title = "VRO API";
  private String description = "VRO Description";
  private String version = "v1";

  @NestedConfigurationProperty private final Contact contact = new Contact();

  @NestedConfigurationProperty private final License license = new License();
}
