package gov.va.vro.abddataaccess.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMqProperties {
  @NotBlank private String claimSubmitExchange;

  @NotBlank private String claimSubmitQueue;
}
