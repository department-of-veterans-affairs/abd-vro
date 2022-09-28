package gov.va.vro.abddataaccess.config.properties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RabbitMqProperties {
  @NotBlank private String claimSubmitExchange;

  @NotBlank private String claimSubmitQueue;
}
