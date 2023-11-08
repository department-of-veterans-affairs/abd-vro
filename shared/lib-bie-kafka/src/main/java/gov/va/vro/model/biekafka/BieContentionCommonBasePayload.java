package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class BieContentionCommonBasePayload extends BieMessageBasePayload {
    private String benefitClaimTypeCode;
    private String actorStation;
    private String details;
    private long veteranParticipantId;
}
