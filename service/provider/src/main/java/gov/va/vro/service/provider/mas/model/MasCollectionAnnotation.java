package gov.va.vro.service.provider.mas.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** @author warren @Date 10/5/22 */
@NoArgsConstructor
@Getter
@Setter
public class MasCollectionAnnotation {
  private int vtrnFileId;
  private String creationDate;
  private List<MasDocument> documents;
}
