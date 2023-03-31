package gov.va.vro.service.provider.bgs.service;

import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.model.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BgsNotesCamelBody {
  public final MasProcessingObject mpo;

  public BgsApiClientRequest request;
  public BgsApiClientResponse response;
}
