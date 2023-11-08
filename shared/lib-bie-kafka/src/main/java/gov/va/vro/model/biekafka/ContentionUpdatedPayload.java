package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class ContentionUpdatedPayload extends BieMessageBasePayload {
    private String journalStatusTypeCode;
    private boolean automationIndicator;
    private String contentionStatusTypeCode;
    private String currentLifecycleStatus;
    private long eventTime;
}
