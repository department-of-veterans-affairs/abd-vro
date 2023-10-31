package gov.va.vro.bip.service;

import gov.va.vro.bip.model.BipClaimResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.RequestForUpdateClaimStatus;
import gov.va.vro.bip.model.UpdateContentionModel;
import gov.va.vro.bip.modelv2.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.modelv2.contentions.GetClaimContentionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqController {

  final BipApiService service;

  @RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "svcBipApiErrorHandler")
  BipClaimResp getClaimDetails(long collectionId) {
    return service.getClaimDetails(collectionId);
  }

  @RabbitListener(queues = "setClaimToRfdStatusQueue", errorHandler = "svcBipApiErrorHandler")
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId) {
    return service.setClaimToRfdStatus(collectionId);
  }

  @RabbitListener(queues = "updateClaimStatusQueue", errorHandler = "svcBipApiErrorHandler")
  BipUpdateClaimResp updateClaimStatus(RequestForUpdateClaimStatus statusAndClaimId) {
    BipUpdateClaimResp result =
        service.updateClaimStatus(statusAndClaimId.getClaimId(), statusAndClaimId.getClaimStatus());
    return result;
  }

  @RabbitListener(queues = "getClaimContentionsQueue", errorHandler = "svcBipApiErrorHandlerV2")
  GetClaimContentionsResponse getClaimContentions(GetClaimContentionsRequest request) {
    return service.getClaimContentions(request.getClaimId());
  }

  @RabbitListener(queues = "updateClaimContentionQueue", errorHandler = "svcBipApiErrorHandler")
  BipUpdateClaimResp updateClaimContention(UpdateContentionModel contention) {
    return service.updateClaimContention(
        contention.getClaimId(), contention.getUpdateContentions());
  }
}
