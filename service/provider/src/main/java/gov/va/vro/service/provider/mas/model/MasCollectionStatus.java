package gov.va.vro.service.provider.mas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** @author warren @Date 10/5/22 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasCollectionStatus {

  @Schema(
      description =
          "Unique identifier for the collection of annotations resulting from OCR and NLP processing of relevant documents")
  @JsonProperty("collectionId")
  private int collectionId;

  @Schema(description = "Status of the collection")
  @JsonProperty("collectionStatus")
  private String collectionStatus;
}
