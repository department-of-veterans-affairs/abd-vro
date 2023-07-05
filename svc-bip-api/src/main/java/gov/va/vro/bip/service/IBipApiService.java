package gov.va.vro.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimContention;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.UpdateContentionReq;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

public interface IBipApiService {
  @RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "errorHandlerForGetClaimDetails")
  BipClaim getClaimDetails(long collectionId) throws JsonProcessingException;

  @RabbitListener(
      queues = "setClaimToRfdStatusQueue",
      errorHandler = "errorHandlerForSetClaimToRfdStatus")
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId) throws BipException;

  BipUpdateClaimResp updateClaimStatus(long collectionId, ClaimStatus status) throws BipException;

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
