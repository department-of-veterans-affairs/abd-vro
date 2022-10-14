package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** @author warren @Date 10/5/22 */
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasCollectionAnnotation {

  @Schema(description = "Veteran File Identifier")
  @JsonProperty("vtrnFileId")
  private int vtrnFileId;

  @Schema(description = "Claim creation date and Time (YYYY-MM-DDThh:mm:ss.sTZD)")
  @JsonProperty("creationDate")
  private String creationDate;

  @Schema(description = "List of documents")
  @JsonProperty("documents")
  private List<MasDocument> documents;
}
