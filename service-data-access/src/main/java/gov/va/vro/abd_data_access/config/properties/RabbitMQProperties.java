package gov.va.vro.abd_data_access.config.properties;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMQProperties {
    @NotBlank
    private String claimSubmitExchange;

    @NotBlank
    private String claimSubmitQueue;
}
