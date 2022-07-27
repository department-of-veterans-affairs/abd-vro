package gov.va.vro.abd_data_access.config.properties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RabbitMQProperties {
  @NotBlank private String claimSubmitExchange;

  @NotBlank private String claimSubmitQueue;
}
