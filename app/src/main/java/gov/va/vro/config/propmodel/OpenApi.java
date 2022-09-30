package gov.va.vro.config.propmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class OpenApi {
  @NestedConfigurationProperty private final Info info = new Info();

  private List<Server> servers = new ArrayList<Server>(Arrays.asList(new Server()));
}
