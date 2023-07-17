package gov.va.vro.bip.service;

import gov.va.vro.bip.model.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class RMQController {
  @Autowired BipApiService service;

  @RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "svcBipApiErrorHandler")
  BipClaim getClaimDetails(long collectionId) {
    return service.getClaimDetails(collectionId);
  }

  @RabbitListener(queues = "setClaimToRfdStatusQueue", errorHandler = "svcBipApiErrorHandler")
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId) {
    return service.setClaimToRfdStatus(collectionId);
  }

  @RabbitListener(queues = "updateClaimStatusQueue", errorHandler = "svcBipApiErrorHandler")
  BipUpdateClaimResp updateClaimStatus(RequestForUpdateClaimStatus statusAndClaimId) {
    System.out.println(statusAndClaimId.toString());
    BipUpdateClaimResp result =
        service.updateClaimStatus(statusAndClaimId.getClaimId(), statusAndClaimId.getClaimStatus());
    return result;
  }

  @RabbitListener(queues = "getClaimContentionsQueue", errorHandler = "svcBipApiErrorHandler")
  BipContentionResp getClaimContentions(long claimId) {
    return new BipContentionResp(service.getClaimContentions(claimId));
  }

  @RabbitListener(queues = "updateClaimContentionQueue", errorHandler = "svcBipApiErrorHandler")
  BipUpdateClaimResp updateClaimContention(UpdateContentionReq contention) {
    return service.updateClaimContention(contention.getClaimId(), contention);
  }
}
