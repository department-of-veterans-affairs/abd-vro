package gov.va.vro.camel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "vro.camel")
public class CamelDtoClassesProperties {
  private List<String> dtoClasses;

  List<Class> getActualDtoClasses() {
    return dtoClasses.stream()
        .map(
            classname -> {
              try {
                return Class.forName(classname);
              } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
              }
            })
        .collect(Collectors.toUnmodifiableList());
  }
}
