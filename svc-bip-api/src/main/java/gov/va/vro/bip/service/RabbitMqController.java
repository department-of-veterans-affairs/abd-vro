package gov.va.vro.bip.service;

import gov.va.vro.bip.model.BipCloseClaimPayload;
import gov.va.vro.bip.model.BipCloseClaimResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.RequestForUpdateClaimStatus;
import gov.va.vro.bip.model.claim.GetClaimRequest;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
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
  GetClaimResponse getClaimDetails(GetClaimRequest request) {
    return service.getClaimDetails(request.getClaimId());
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

  @RabbitListener(queues = "createClaimContentionsQueue", errorHandler = "bipRequestErrorHandler")
  CreateClaimContentionsResponse createClaimContentions(CreateClaimContentionsRequest request) {
    return service.createClaimContentions(request);
  }

  @RabbitListener(queues = "updateClaimContentionsQueue", errorHandler = "bipRequestErrorHandler")
  UpdateClaimContentionsResponse updateClaimContentions(UpdateClaimContentionsRequest request) {
    return service.updateClaimContentions(request);
  }

  @RabbitListener(queues = "cancelClaimQueue", errorHandler = "bipRequestErrorHandler")
  BipCloseClaimResp cancelClaim(BipCloseClaimPayload cancelRequest) {
    return service.cancelClaim(cancelRequest);
  }

  @RabbitListener(
      queues = "putTempStationOfJurisdictionQueue",
      errorHandler = "bipRequestErrorHandler")
  PutTempStationOfJurisdictionResponse putTempStationOfJurisdictionEndpoint(
      PutTempStationOfJurisdictionRequest request) {
    return service.putTempStationOfJurisdiction(request);
  }
}
