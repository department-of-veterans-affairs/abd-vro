package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * MAS collection annotation class.
 *
 * @author warren @Date 10/5/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasCollectionAnnotation {
  @JsonProperty("collectionsId")
  private int collectionsId;

  @JsonProperty("vtrnFileId")
  private String veteranFileId;

  @JsonProperty("creationDate")
  private String creationDate;

  @JsonProperty("documents")
  private List<MasDocument> documents;

  @JsonProperty("documentsWithoutAnnotationsChecked")
  private List<String> documentsWithoutAnnotationsChecked;
}
