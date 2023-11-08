package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class ContentionAssociatedToClaimPayload extends BieMessageBasePayload {
    private boolean automationIndicator;
    private String contentionStatusTypeCode;
    private String currentLifecycleStatus;
    private long eventTime;
}
