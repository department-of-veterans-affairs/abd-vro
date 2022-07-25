package gov.va.vro.abd_data_access.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Lighthouse token message.
 *
 *  @author Warren Lin
 */
@Setter
@Getter
@NoArgsConstructor
public class LighthouseTokenMessage {
    private String access_token;
    private String token_type;
    private String scope;
    private int expires_in;
    private String state;
    private String patient;
}
