package gov.va.vro.propmodel;

import gov.va.vro.propmodel.Info;
import gov.va.vro.propmodel.Server;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "vro.openapi")
public class OpenApiProperties {
  private final Info info = new Info();

  private List<Server> servers = new ArrayList<Server>(Arrays.asList(new Server()));
}
