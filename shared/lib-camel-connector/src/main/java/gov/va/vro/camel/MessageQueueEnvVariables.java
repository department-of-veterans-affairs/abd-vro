package gov.va.vro.camel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
class MessageQueueEnvVariables {
  @Value("${RABBITMQ_PLACEHOLDERS_HOST:#{null}}")
  private String host;

  @Value("${RABBITMQ_PLACEHOLDERS_PORT:0}")
  private int port = 0;

  @Value("${RABBITMQ_PLACEHOLDERS_USERNAME:#{null}}")
  private String username;

  @Value("${RABBITMQ_PLACEHOLDERS_USERPASSWORD:#{null}}")
  private String password;
}
