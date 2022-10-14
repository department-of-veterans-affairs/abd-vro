package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** @author warren @Date 10/5/22 */
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasAnnotation {

  @Schema(description = "Annotation Type")
  @JsonProperty("annotType")
  private String annotType;

  @Schema(description = "Page Number")
  @JsonProperty("pageNum")
  private String pageNum;

  @Schema(description = "Annotation Name")
  @JsonProperty("annotName")
  private String annotName;

  @Schema(description = "Annotation Value")
  @JsonProperty("annotVal")
  private String annotVal;

  @Schema(description = "Spellcheck Value")
  @JsonProperty("spellCheckVal")
  private String spellCheckVal;

  @Schema(description = "Observation Date and Time (YYYY-MM-DDThh:mm:ss.sTZD)")
  @JsonProperty("observationDate")
  private String observationDate;

  @Schema(description = "Start Value")
  @JsonProperty("start")
  private int start;

  @Schema(description = "End Value")
  @JsonProperty("end")
  private int end;

  @Schema(description = "Acd Pref Name")
  @JsonProperty("acdPrefName")
  private String acdPrefName;

  @Schema(description = "Is it relevant")
  @JsonProperty("relevant")
  private boolean relevant;
}
