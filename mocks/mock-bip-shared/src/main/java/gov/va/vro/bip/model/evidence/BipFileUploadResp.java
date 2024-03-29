package gov.va.vro.bip.model.evidence;

import gov.va.vro.bip.model.evidence.response.UploadResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * BIP file upload response.
 *
 * @author warren @Date 12/7/22
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BipFileUploadResp {
  private HttpStatus status;
  private String message;
  private UploadResponse uploadResponse;
}
