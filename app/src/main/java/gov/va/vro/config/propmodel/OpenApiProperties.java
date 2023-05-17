package gov.va.vro.config.propmodel;

import gov.va.vro.config.propmodel.Server;
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
  private List<Server> servers = new ArrayList<Server>(Arrays.asList(new Server()));
}
