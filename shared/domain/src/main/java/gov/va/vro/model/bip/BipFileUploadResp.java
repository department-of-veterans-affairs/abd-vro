package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/** @author warren @Date 12/7/22 */
@RequiredArgsConstructor
@Getter
@Setter
public class BipFileUploadResp {
  private HttpStatus status;
  private String message;
}
