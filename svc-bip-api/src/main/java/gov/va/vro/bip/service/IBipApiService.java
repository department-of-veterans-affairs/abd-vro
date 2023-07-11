package gov.va.vro.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.va.vro.bip.config.UpdateClaimStatusConfig;
import gov.va.vro.bip.model.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

public interface IBipApiService {

  //todo: move annotation to implementation
  //todo: change queue names to @Value variables
  @RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "errorHandlerForGetClaimDetails")
  BipClaim getClaimDetails(long collectionId) throws JsonProcessingException;

  @RabbitListener(
      queues = "setClaimToRfdStatusQueue",
      errorHandler = "errorHandlerForSetClaimToRfdStatus")
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId) throws BipException;
  @RabbitListener(
      queues = "updateClaimStatusQueue",
      errorHandler = "errorHandlerForUpdateClaimStatus")
  BipUpdateClaimResp updateClaimStatus(RequestForUpdateClaimStatus statusAndClaimId) throws BipException;

  List<ClaimContention> getClaimContentions(long claimId) throws BipException;

  BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
      throws BipException;

  /**
   * Verifies a call to the BIP Claims API can be made by getting document types.
   *
   * @return boolean verification status
   */
  boolean verifySpecialIssueTypes();
}
