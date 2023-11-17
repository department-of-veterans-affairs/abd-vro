package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ContentionsApi;
import gov.va.vro.mockbipclaims.mapper.ContentionMapper;
import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.ExistingContention;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateContentionsResponse;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.store.UpdatesStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ContentionsController extends BaseController implements ContentionsApi {
  private final ClaimStore claimStore;

  private final UpdatesStore actionStore;

  private final ContentionMapper mapper;

  @Override
  public ResponseEntity<ContentionSummariesResponse> getContentionsForClaim(Long claimId) {
    log.info("Getting contentions for claim (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    ContentionSummariesResponse response = new ContentionSummariesResponse();

    if (item == null) {
      return createClaim404(response, claimId);
    }
    if (claimId == 500) {
      return create500(response);
    }

    List<ContentionSummary> contentions = item.getContentions();

    response.setContentions(contentions);

    return create200(response);
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
    UpdateContentionsResponse response = new UpdateContentionsResponse();
    if (item == null) {
      // Non-existent claim id yields 400, not 404
      return createClaim400(response, claimId);
    }
    if (claimId == 500) {
      return create500(response);
    }

    List<ExistingContention> contentions = updateContentionsRequest.getUpdateContentions();
    List<ContentionSummary> currentContentions = item.getContentions();
    for (ExistingContention contention : contentions) {
      Long id = contention.getContentionId();
      int existingIndex = findContention(currentContentions, id);
      if (existingIndex < 0) {
        return createContention400(response, claimId, id);
      }

      ContentionSummary summary = mapper.toContentionSummary(contention);
      summary.setLastModified(OffsetDateTime.now());

      currentContentions.set(existingIndex, summary);
    }
    actionStore.addContentionsUpdate(claimId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /** Not fully implemented. Only used for connectivity testing. */
  @Override
  public ResponseEntity<String> getSpecialIssueTypes() {
    log.info("Returning an empty array as special issues...");
    return new ResponseEntity<>("[]", HttpStatus.OK);
  }
}
