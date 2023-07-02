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
    @RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "rabbitListenerErrorHandler")
    BipClaim getClaimDetails(long collectionId) throws JsonProcessingException;

    @RabbitListener(queues = "setClaimToRfdStatusQueue", errorHandler = "rabbitListenerErrorHandler")
    BipUpdateClaimResp setClaimToRfdStatus(long collectionId) throws BipException;

    @RabbitListener(queues = "updateClaimStatusQueue", errorHandler = "rabbitListenerErrorHandler")
    BipUpdateClaimResp updateClaimStatus(long collectionId, ClaimStatus status) throws BipException;

    @RabbitListener(queues = "getClaimContentionsQueue", errorHandler = "rabbitListenerErrorHandler")
    List<ClaimContention> getClaimContentions(long claimId) throws BipException;

    @RabbitListener(queues = "updateClaimContentionQueue", errorHandler = "rabbitListenerErrorHandler")
    BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
            throws BipException;

    /**
     * Verifies a call to the BIP Claims API can be made by getting document types.
     *
     * @return boolean verification status
     */
    @RabbitListener(queues = "verifySpecialIssueTypesQueue", errorHandler = "rabbitListenerErrorHandler")
    boolean verifySpecialIssueTypes();
}
