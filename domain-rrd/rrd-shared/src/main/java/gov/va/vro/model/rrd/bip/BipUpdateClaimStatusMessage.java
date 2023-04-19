package gov.va.vro.model.rrd.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Data
public class BipUpdateClaimStatusMessage {
  private String severity;
  private String test;
  private String key;
  private int status;
  private String timestamp;
}
