package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/** Model for a message that are sent in REST responses. */
@Data
@Schema(name = "message", description = "Relays information in REST responses")
public class Message {
  @NotNull private String key;

  @NotNull private String severity;

  private Integer status;

  private String text;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;
}
