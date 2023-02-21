package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MAS annotation class.
 *
 * @author warren @Date 10/5/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasAnnotation {
  @JsonProperty("annot_type")
  private String annotType;

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

  @JsonProperty("annot_val")
  private String annotVal;

  @JsonProperty("spellcheck_val")
  private String spellCheckVal;

  @JsonProperty("observation_date")
  private String observationDate;

  @JsonProperty("start")
  private int start;

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
