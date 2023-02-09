package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockbipclaims.model.Message;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(value = {ObjectMapper.class})
public class ModelMapperTest {
  @Autowired private ObjectMapper objectMapper;

  @SneakyThrows
  @Test
  void messagePositiveTest() {
    Message message = new Message();
    message.setKey("the key");
    message.setSeverity("the severity");
    message.setText("the text");

    String jsonMessage = objectMapper.writeValueAsString(message);
    Message readBackMessage = objectMapper.readValue(jsonMessage, Message.class);
    assertEquals(message.getKey(), readBackMessage.getKey());
    assertEquals(message.getSeverity(), readBackMessage.getSeverity());
    assertEquals(message.getText(), readBackMessage.getText());
  }
}
