package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ClaimsApi;
import gov.va.vro.mockbipclaims.configuration.ClaimStore;
import gov.va.vro.mockbipclaims.configuration.ClaimStoreItem;
import gov.va.vro.mockbipclaims.mapper.ContentionMapper;
import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ClaimLifecycleStatusesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
import gov.va.vro.mockbipclaims.model.CreateContentionsRequest;
import gov.va.vro.mockbipclaims.model.CreateContentionsResponse;
import gov.va.vro.mockbipclaims.model.ExistingContention;
import gov.va.vro.mockbipclaims.model.Message;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusResponse;
import gov.va.vro.mockbipclaims.model.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.UpdateContentionsResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ClaimsApiController implements ClaimsApi {
  @Autowired private ClaimStore claimStore;

  @Autowired private ContentionMapper mapper;

  @Override
  public ResponseEntity<CreateContentionsResponse> createContentions(
      Long claimId, CreateContentionsRequest createContentionsRequest) {
    return null;
  }

  @Override
  public ResponseEntity<ContentionSummariesResponse> getContentionsForClaim(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    List<ContentionSummary> contentions = item.getContentions();

    ContentionSummariesResponse response = new ContentionSummariesResponse();
    response.setContentions(contentions);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimDetailResponse> getClaimById(Long claimId) {
    ClaimStoreItem item = claimStore.get(claimId);
    ClaimDetail claimDetail = item.getClaimDetail();

    ClaimDetailResponse response = new ClaimDetailResponse();
    response.setClaim(claimDetail);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimLifecycleStatusesResponse> getClaimLifecycleStatuses(
      Long claimId, Boolean includeHistory) {
    return null;
  }

  @Override
  public ResponseEntity<UpdateClaimLifecycleStatusResponse> updateClaimLifecycleStatus(
      Long claimId, UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest) {
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }
    String status = updateClaimLifecycleStatusRequest.getClaimLifecycleStatus();
    item.getClaimDetail().setClaimLifecycleStatus(status);
    var response = new UpdateClaimLifecycleStatusResponse();
    Message message = new Message();
    message.setText("Success");
    response.addMessagesItem(message);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private static int findContention(List<ContentionSummary> contentions, Long contentionId) {
    for (int index = 0; index < contentions.size(); ++index) {
      if (contentionId.equals(contentions.get(index).getContentionId())) {
        return index;
      }
    }
    return -1;
  }

  @SneakyThrows
  @Override
  public ResponseEntity<UpdateContentionsResponse> updateContentions(
      Long claimId, UpdateContentionsRequest updateContentionsRequest) {
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }

    List<ExistingContention> contentions = updateContentionsRequest.getUpdateContentions();
    List<ContentionSummary> currentContensions = item.getContentions();
    for (ExistingContention contention : contentions) {
      Long id = contention.getContentionId();
      int existingIndex = findContention(currentContensions, id);
      if (existingIndex < 0) {
        String reason = "Contention does not exist in claim for id: " + claimId;
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
      }

      ContentionSummary summary = mapper.toContentionSummary(contention);
      String now = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
      summary.setLastModified(OffsetDateTime.now());

      currentContensions.set(existingIndex, summary);
    }
    UpdateContentionsResponse response = new UpdateContentionsResponse();
    Message message = new Message();
    message.setText("Success");
    response.addMessagesItem(message);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
