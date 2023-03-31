package gov.va.vro.service.provider.bgs.service;

import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.model.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@RequiredArgsConstructor
public class BgsNotesCamelBody {
  public final MasProcessingObject mpo;

  public AtomicInteger tryCount = new AtomicInteger(1);

  public BgsNotesCamelBody incrementTryCount() {
    tryCount.incrementAndGet();
    return this;
  }

  // TODO: might be multiple requests, in which case we need to distinguish completed requests
  public BgsApiClientRequest request;
  public BgsApiClientResponse response;
}
