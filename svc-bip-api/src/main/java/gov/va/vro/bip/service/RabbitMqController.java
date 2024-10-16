package gov.va.vro.bip.service;

import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import gov.va.vro.bip.model.claim.GetClaimRequest;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesRequest;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleRequest;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class RabbitMqController {

  final BipApiService service;

  @RabbitListener(queues = "#{@getClaimDetailsQueue}", errorHandler = "bipRequestErrorHandler")
  GetClaimResponse getClaimDetails(@Valid @Payload GetClaimRequest request) {
    return service.getClaimDetails(request);
  }

  @RabbitListener(
      queues = "#{@putClaimLifecycleStatusQueue}",
      errorHandler = "bipRequestErrorHandler")
  PutClaimLifecycleResponse updateClaimStatus(@Valid @Payload PutClaimLifecycleRequest request) {
    return service.putClaimLifecycleStatus(request);
  }

  @RabbitListener(queues = "#{@getClaimContentionsQueue}", errorHandler = "bipRequestErrorHandler")
  GetClaimContentionsResponse getClaimContentions(
      @Valid @Payload GetClaimContentionsRequest request) {
    return service.getClaimContentions(request);
  }

  @RabbitListener(
      queues = "#{@createClaimContentionsQueue}",
      errorHandler = "bipRequestErrorHandler")
  CreateClaimContentionsResponse createClaimContentions(
      @Valid @Payload CreateClaimContentionsRequest request) {
    return service.createClaimContentions(request);
  }

  @RabbitListener(
      queues = "#{@updateClaimContentionsQueue}",
      errorHandler = "bipRequestErrorHandler")
  UpdateClaimContentionsResponse updateClaimContentions(
      @Valid @Payload UpdateClaimContentionsRequest request) {
    return service.updateClaimContentions(request);
  }

  @RabbitListener(queues = "#{@cancelClaimQueue}", errorHandler = "bipRequestErrorHandler")
  CancelClaimResponse cancelClaim(@Valid @Payload CancelClaimRequest cancelRequest) {
    return service.cancelClaim(cancelRequest);
  }

  @RabbitListener(
      queues = "#{@putTempStationOfJurisdictionQueue}",
      errorHandler = "bipRequestErrorHandler")
  PutTempStationOfJurisdictionResponse putTempStationOfJurisdictionEndpoint(
      @Valid @Payload PutTempStationOfJurisdictionRequest request) {
    return service.putTempStationOfJurisdiction(request);
  }

  @RabbitListener(queues = "#{@getSpecialIssueTypesQueue}", errorHandler = "bipRequestErrorHandler")
  GetSpecialIssueTypesResponse getSpecialIssueTypesEndpoint(GetSpecialIssueTypesRequest request) {
    return service.getSpecialIssueTypes(request);
  }
}
