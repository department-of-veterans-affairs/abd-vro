package gov.va.vro.service.provider.bgs.service;

import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.model.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@RequiredArgsConstructor
public class BgsNotesCamelBody {
  public final MasProcessingObject mpo;

  public AtomicInteger tryCount = new AtomicInteger(1);

  /*
  From BGS team: The transaction time out configured on our internal domain is 120 seconds. So, I
  would recommend that reasonable interval between retires should be at least 120 seconds for
  transient faults to avoid any collusion (maybe use exponential back-off for retry if
  transaction is carried out in an automated fashion). I guess limit the number of retries to
  about 3 to 5 if the operation is part of user interaction on your side.
  */
  public final int delayBaseMillis;
  public int delayMillis;

  public BgsNotesCamelBody incrementTryCount() {
    // Use exponential backoff for retries. The random term helps to avoid cases where many clients
    // are synchronized by some situation and all retry at once.
    delayMillis =
        delayBaseMillis * ((int) Math.pow(2, tryCount.get() - 1)) + new Random().nextInt(2000);
    tryCount.incrementAndGet();
    return this;
  }

  // Since there are potentially several notes, we have to split up requests
  public List<BgsApiClientRequest> pendingRequests = new ArrayList<>();

  public BgsApiClientRequest currentRequest() {
    request = pendingRequests.get(0);
    response = null;
    return request;
  }

  // Called after successful request or done retrying
  public void removeRequest(BgsApiClientRequest request) {
    pendingRequests.remove(request);
    tryCount.set(1);
  }

  // current request and response objects
  public BgsApiClientRequest request;
  public BgsApiClientResponse response;
}
