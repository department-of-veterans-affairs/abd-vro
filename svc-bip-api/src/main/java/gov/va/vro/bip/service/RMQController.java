package gov.va.vro.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.va.vro.bip.model.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class RMQController {
	@Autowired BipApiService service;

	@RabbitListener(queues = "getClaimDetailsQueue", errorHandler = "errorHandlerForGetClaimDetails")
	BipClaim getClaimDetails(long collectionId) throws JsonProcessingException{
		return service.getClaimDetails(collectionId);
	}//1

	@RabbitListener(
		queues = "setClaimToRfdStatusQueue",
		errorHandler = "errorHandlerForSetClaimToRfdStatus")
	BipUpdateClaimResp setClaimToRfdStatus(long collectionId) {
		return service.setClaimToRfdStatus(collectionId);
	}; //2


	@RabbitListener(
		queues = "updateClaimStatusQueue",
		errorHandler = "errorHandlerForUpdateClaimStatus")
	BipUpdateClaimResp updateClaimStatus(RequestForUpdateClaimStatus statusAndClaimId) throws BipException{
		System.out.println(statusAndClaimId.toString());
		BipUpdateClaimResp result = service.updateClaimStatus(statusAndClaimId.getClaimId(), statusAndClaimId.getClaimStatus());
		return result;
	}
	@RabbitListener(
		queues = "getClaimContentionsQueue",
		errorHandler = "errorHandlerForGetClaimContentions")
	BipContentionResp getClaimContentions(long claimId) throws BipException{
		return new BipContentionResp(service.getClaimContentions(claimId));
	}
	@RabbitListener(
		queues = "updateClaimContentionQueue",
		errorHandler = "errorHandlerForUpdateClaimContention")
	BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
		throws BipException{
		return service.updateClaimContention(claimId, contention);
	}
	@RabbitListener(
		queues = "confirmCanCallSpecialIssueTypesQueue",
		errorHandler = "errorHandlerForConfirmCanCallSpecialIssueTypes")
	boolean confirmCanCallSpecialIssueTypes(){
		return service.confirmCanCallSpecialIssueTypes();
	}

}
