package gov.va.vro.service.provider.mas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** @author warren @Date 10/5/22 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasCollectionAnnotation {
  @JsonProperty("collectionsId")
  private int collectionsId;

  @JsonProperty("vtrnFileId")
  private String vtrnFileId;

  @JsonProperty("creationDate")
  private String creationDate;

  @JsonProperty("documents")
  private List<MasDocument> documents;
}
