package gov.va.vro.config.propmodel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class OpenApi {
  private final Info info = new Info();

  private List<Server> servers = new ArrayList<Server>(Arrays.asList(new Server()));
}
