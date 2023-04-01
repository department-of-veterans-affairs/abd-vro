package gov.va.vro.service.provider.bgs.service;

import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.model.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@RequiredArgsConstructor
public class BgsNotesCamelBody {
  public final MasProcessingObject mpo;

  public AtomicInteger tryCount = new AtomicInteger(1);

  // From BGS team: The transaction time out configured on our internal domain is 120 seconds. So, I
  // would recommend that reasonable interval between retires should be at least 120 seconds for
  // transient faults to avoid any collusion (maybe use exponential back-off for retry if
  // transaction is carried out in an automated fashion). I guess limit the number of retries to
  // about 3 to 5 if the operation is part of user interaction on your side.
  public static final int DELAY_BASE_MILLIS = 130_000;
  public int delayMillis;

  public BgsNotesCamelBody incrementTryCount() {
    // Use exponential backoff for retries. The random term helps to avoid cases where many clients
    // are synchronized by some situation
    // and all retry at once
    delayMillis =
        DELAY_BASE_MILLIS * ((int) Math.pow(2, tryCount.get() - 1)) + new Random().nextInt(2000);
    tryCount.incrementAndGet();
    return this;
  }

  // TODO: might be multiple requests, in which case we need to distinguish completed requests
  public BgsApiClientRequest request;
  public BgsApiClientResponse response;
}
