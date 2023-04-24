package gov.va.vro.model.rrd.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MAS annotation class. Fields are populated by automated processing of supporting documents
 * related to a given claim. A single @code{MasDocument} may be associated with
 * multiple @code{MasAnnotation}s.
 *
 * @author warren @Date 10/5/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasAnnotation {
  // Maps to one of the values in the enum type @code{MasAnnotType}
  @JsonProperty("annot_type")
  private String annotType;

  // Refers to the location of the extracted @code{annotVal} text within the document
  @JsonProperty("page_num")
  private String pageNum;

  @JsonProperty("eFolderVersionRefId")
  private String eFolderVersionRefId;

  @JsonProperty("dates")
  private String dates;

  @JsonProperty("recDate")
  private String recDate;

  @JsonProperty("docTypedescription")
  private String docTypedescription;

  @JsonProperty("annot_name")
  private String annotName;

  // Raw text extracted from the document
  @JsonProperty("annot_val")
  private String annotVal;

  @JsonProperty("spellcheck_val")
  private String spellCheckVal;

  @JsonProperty("observation_date")
  private String observationDate;

  // Beginning location of @code{annotVal} on @code{pageNum}
  @JsonProperty("start")
  private int start;

  // Ending location of @code{annotVal} on @code{pageNum}
  @JsonProperty("end")
  private int end;

  @JsonProperty("acd_pref_name")
  private String acdPrefName;

  @JsonProperty("relevant")
  private boolean relevant;

  @JsonProperty("partial_date")
  private String partialDate;

  @JsonProperty("sorted_date")
  private String sortedDate;
}
