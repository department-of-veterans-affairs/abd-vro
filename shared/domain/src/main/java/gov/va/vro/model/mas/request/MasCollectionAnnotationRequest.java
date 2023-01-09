package gov.va.vro.model.mas.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MAS collection annotation request class.
 *
 * @author warren @Date 10/5/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasCollectionAnnotationRequest {
  @JsonProperty("collectionsId")
  private int collectionsId;
}
