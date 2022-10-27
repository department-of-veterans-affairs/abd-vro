package gov.va.vro.model.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.function.Function;

public class JsonConverter implements Function<Auditable, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SneakyThrows
  public String apply(Auditable auditable) {
    return objectMapper.writeValueAsString(auditable);
  }
}
