package gov.va.vro.services.bie.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BieKafkaException extends RuntimeException {
    public BieKafkaException(String message, Throwable cause) {
        super(message, cause);
    }

    public BieKafkaException(String message) {
        super(message);
    }
}
