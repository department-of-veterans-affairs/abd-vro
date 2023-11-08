package gov.va.vro.bip.service;

import gov.va.vro.bip.model.BipClaimResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.RequestForUpdateClaimStatus;
import gov.va.vro.bip.model.UpdateContentionModel;
import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqController {

  final BipApiService service;

  @RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "bipRequestErrorHandler")
  BipClaimResp getClaimDetails(long collectionId) {
    return service.getClaimDetails(collectionId);
  }

  @RabbitListener(queues = "setClaimToRfdStatusQueue", errorHandler = "bipRequestErrorHandler")
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId) {
    return service.setClaimToRfdStatus(collectionId);
  }

  @RabbitListener(queues = "updateClaimStatusQueue", errorHandler = "bipRequestErrorHandler")
  BipUpdateClaimResp updateClaimStatus(RequestForUpdateClaimStatus statusAndClaimId) {
    return service.updateClaimStatus(
        statusAndClaimId.getClaimId(), statusAndClaimId.getClaimStatus());
  }

  @RabbitListener(queues = "getClaimContentionsQueue", errorHandler = "bipRequestErrorHandler")
  GetClaimContentionsResponse getClaimContentions(GetClaimContentionsRequest request) {
    return service.getClaimContentions(request.getClaimId());
  }

  @RabbitListener(queues = "updateClaimContentionQueue", errorHandler = "bipRequestErrorHandler")
  BipUpdateClaimResp updateClaimContention(UpdateContentionModel contention) {
    return service.updateClaimContention(
        contention.getClaimId(), contention.getUpdateContentions());
  }

  @RabbitListener(
      queues = "putTempStationOfJurisdictionQueue",
      errorHandler = "bipRequestErrorHandler")
  PutTempStationOfJurisdictionResponse putTempStationOfJurisdictionEndpoint(
      PutTempStationOfJurisdictionRequest request) {
    return service.putTempStationOfJurisdiction(request);
  }
}
