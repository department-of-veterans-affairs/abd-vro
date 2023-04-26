package gov.va.vro.model.rrd.mas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MAS collection status class.
 *
 * @author warren @Date 10/5/22
 */
@Getter
@Setter
@NoArgsConstructor
public class MasCollectionStatus {
  private int collectionsId;
  private String collectionStatus;
}
