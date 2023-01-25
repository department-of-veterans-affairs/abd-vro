package gov.va.vro.model.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.function.Function;

/** Maps an object to a JSON String. */
public class JsonConverter implements Function<Auditable, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SneakyThrows
  public String apply(Auditable auditable) {
    return objectMapper.writeValueAsString(auditable);
  }
}
