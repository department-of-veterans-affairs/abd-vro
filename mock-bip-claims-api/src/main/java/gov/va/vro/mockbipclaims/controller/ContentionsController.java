package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ContentionsApi;
import gov.va.vro.mockbipclaims.configuration.ClaimStore;
import gov.va.vro.mockbipclaims.configuration.ClaimStoreItem;
import gov.va.vro.mockbipclaims.mapper.ContentionMapper;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
import gov.va.vro.mockbipclaims.model.ExistingContention;
import gov.va.vro.mockbipclaims.model.Message;
import gov.va.vro.mockbipclaims.model.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.UpdateContentionsResponse;
import gov.va.vro.mockbipclaims.model.store.UpdatesStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ContentionsController implements ContentionsApi {
  private final ClaimStore claimStore;

  private final UpdatesStore actionStore;

  private final ContentionMapper mapper;

  @Override
  public ResponseEntity<ContentionSummariesResponse> getContentionsForClaim(Long claimId) {
    log.info("Getting contentions for claim (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }
    List<ContentionSummary> contentions = item.getContentions();

    ContentionSummariesResponse response = new ContentionSummariesResponse();
    response.setContentions(contentions);

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
    log.info("Updating contentions claim (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    if (item == null) {
      String reason = "No claim found for id: " + claimId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }

    List<ExistingContention> contentions = updateContentionsRequest.getUpdateContentions();
    List<ContentionSummary> currentContentions = item.getContentions();
    for (ExistingContention contention : contentions) {
      Long id = contention.getContentionId();
      int existingIndex = findContention(currentContentions, id);
      if (existingIndex < 0) {
        String reason = "Contention does not exist in claim for id: " + claimId;
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
      }

      ContentionSummary summary = mapper.toContentionSummary(contention);
      summary.setLastModified(OffsetDateTime.now());

      currentContentions.set(existingIndex, summary);
    }
    UpdateContentionsResponse response = new UpdateContentionsResponse();
    Message message = new Message();
    message.setText("Success");
    response.addMessagesItem(message);
    actionStore.addContentionsUpdate(claimId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
