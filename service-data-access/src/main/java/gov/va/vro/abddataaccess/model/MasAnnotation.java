package gov.va.vro.abddataaccess.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** @author warren @Date 10/5/22 */
@NoArgsConstructor
@Getter
@Setter
public class MasAnnotation {
  private String annotType;
  private String pageNum;
  private String annotName;
  private String annotVal;
  private String spellCheckVal;
  private String observationDate;
  private int start;
  private int end;
  private String acdPrefName;
  private boolean relevant;
}
