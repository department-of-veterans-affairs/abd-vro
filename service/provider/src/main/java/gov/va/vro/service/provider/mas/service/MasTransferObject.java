package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MasTransferObject implements Auditable {

  @NonNull private final MasAutomatedClaimPayload claimPayload;
  @NonNull private final AbdEvidence evidence;

  @Override
  public String getEventId() {
    return claimPayload.getEventId();
  }
}
