package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * BIP contention API response.
 *
 * @author warren @Date 11/11/22
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // BIP API can send messages
public class BipContentionResp extends HasStatusCodeAndMessage{
  public BipContentionResp(List<ClaimContention> contentions){
    this.contentions = contentions;
  }
  private List<ClaimContention> contentions;
}
