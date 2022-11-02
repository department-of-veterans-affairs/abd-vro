package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class BipUpdateClaimStatusResp {
  private List<BipUpdateClaimStatusMessage> messages;
}
