package gov.va.vro.model.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * BIP contention API response.
 *
 * @author warren @Date 11/11/22
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // BIP API can send messages
public class BipContentionResp {
  private List<ClaimContention> contentions;
}
