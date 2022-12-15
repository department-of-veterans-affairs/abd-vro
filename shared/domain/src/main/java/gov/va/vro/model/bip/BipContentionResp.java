package gov.va.vro.model.bip;

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
public class BipContentionResp {
  private List<ClaimContention> contentions;
}
