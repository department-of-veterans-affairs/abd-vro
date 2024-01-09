package gov.va.vro.mockbipclaims.controller;

import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_ALL_ENDPOINTS_YIELDS_500;
import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_CREATE_CONTENTIONS_YIELDS_500;
import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_GET_CONTENTIONS_YIELDS_500;
import static gov.va.vro.mockbipclaims.config.ClaimIdConstants.CLAIM_ID_UPDATE_CONTENTIONS_YIELDS_500;

import gov.va.vro.mockbipclaims.api.ContentionsApi;
import gov.va.vro.mockbipclaims.mapper.ContentionMapper;
import gov.va.vro.mockbipclaims.model.bip.ContentionSummary;
import gov.va.vro.mockbipclaims.model.bip.ExistingContention;
import gov.va.vro.mockbipclaims.model.bip.request.CreateContentionsRequest;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.bip.response.CreateContentionsResponse;
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
import java.util.Optional;
import java.util.Random;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ContentionsController extends BaseController implements ContentionsApi {
  private final ClaimStore claimStore;

  private final UpdatesStore actionStore;

  private final ContentionMapper mapper;

  public ResponseEntity<CreateContentionsResponse> createContentionsForClaim(
      Long claimId, CreateContentionsRequest createContentionsRequest) {
    log.info("Creating contentions for claim (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    CreateContentionsResponse response = new CreateContentionsResponse();

    if (item == null) {
      return createClaim400(response, claimId);
    }
    if (claimId == CLAIM_ID_ALL_ENDPOINTS_YIELDS_500 || claimId == CLAIM_ID_CREATE_CONTENTIONS_YIELDS_500) {
      return create500(response);
    }

    Optional.ofNullable(createContentionsRequest.getCreateContentions())
        .orElse(List.of())
        .forEach(
            contention -> {
              long contentionId = new Random().nextLong();
              ContentionSummary summary = mapper.toContentionSummary(contention);
              summary.setLastModified(OffsetDateTime.now());
              summary.setContentionId(contentionId);

              item.getContentions().add(summary);
              response.addContentionId(contentionId);
            });

    return create201(response);
  }

  @Override
  public ResponseEntity<ContentionSummariesResponse> getContentionsForClaim(Long claimId) {
    log.info("Getting contentions for claim (id: {})", claimId);
    ClaimStoreItem item = claimStore.get(claimId);
    ContentionSummariesResponse response = new ContentionSummariesResponse();

    if (item == null) {
      return createClaim404(response, claimId);
    }
    if (claimId == CLAIM_ID_ALL_ENDPOINTS_YIELDS_500 || claimId == CLAIM_ID_GET_CONTENTIONS_YIELDS_500) {
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
    if (claimId == CLAIM_ID_ALL_ENDPOINTS_YIELDS_500 || claimId == CLAIM_ID_UPDATE_CONTENTIONS_YIELDS_500) {
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
