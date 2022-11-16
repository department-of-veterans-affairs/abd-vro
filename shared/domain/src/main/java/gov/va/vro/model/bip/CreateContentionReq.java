package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request to create contentions for a claim.
 *
 * @author warren @Date 11/14/22
 */
@Getter
@Setter
@RequiredArgsConstructor
public class CreateContentionReq {
  List<CreateContention> createContentions;
}
