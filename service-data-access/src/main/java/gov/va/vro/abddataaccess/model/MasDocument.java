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
public class MasDocument {

  @Schema(description = "eFolder version Reference ID")
  @JsonProperty("eFolderVersionRefId")
  private int efolderversionrefid;

  @Schema(description = "Claims condition")
  @JsonProperty("condition")
  private String condition;

  @Schema(description = "List of Annotations")
  @JsonProperty("annotations")
  private List<MasAnnotation> annotations;
}
