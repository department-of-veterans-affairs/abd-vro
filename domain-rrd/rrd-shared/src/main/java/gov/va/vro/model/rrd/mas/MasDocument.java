package gov.va.vro.model.rrd.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * MAS document class.
 *
 * @author warren @Date 10/5/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasDocument {

  @JsonProperty("docTypeDescription")
  private String docTypeDescription;

  @JsonProperty("eFolderVersionRefId")
  private String efolderversionrefid;

  @JsonProperty("recDate")
  private String recDate;

  @JsonProperty("condition")
  private String condition;

  @JsonProperty("annotations")
  private List<MasAnnotation> annotations;
}
