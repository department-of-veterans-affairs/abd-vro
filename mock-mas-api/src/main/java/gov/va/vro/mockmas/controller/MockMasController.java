package gov.va.vro.mockmas.controller;

import gov.va.vro.mockmas.config.MasApiService;
import gov.va.vro.mockmas.model.CollectionStore;
import gov.va.vro.mockmas.model.MasTokenResponse;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasCollectionStatus;
import gov.va.vro.model.mas.MasStatus;
import gov.va.vro.model.mas.request.MasCollectionAnnotationRequest;
import gov.va.vro.model.mas.request.MasCollectionStatusRequest;
import gov.va.vro.model.mas.request.MasOrderExamRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class MockMasController {
  private final MasApiService apiService;
  private final CollectionStore store;

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/pcQueryCollectionAnnots",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<List<MasCollectionAnnotation>> getAnnotations(
      @RequestBody MasCollectionAnnotationRequest request) {
    int collectionId = request.getCollectionsId();
    log.info("Received annotations request for collection id: {}", collectionId);
    if (collectionId == 350) {
      List<MasCollectionAnnotation> collection = apiService.getAnnotation(collectionId);
      return new ResponseEntity<>(collection, HttpStatus.OK);
    }

    List<MasCollectionAnnotation> collection = store.get(collectionId);
    if (collection == null) {
      String reason = "No claim found for id: " + collectionId;
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
    }

    return new ResponseEntity<>(collection, HttpStatus.OK);
  }

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/pcCheckCollectionStatus",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<List<MasCollectionStatus>> getCollectionStatus(
      @RequestBody MasCollectionStatusRequest request) {
    int collectionId = request.getCollectionsId();
    MasCollectionStatus status = new MasCollectionStatus();
    status.setCollectionsId(collectionId);
    status.setCollectionStatus(MasStatus.VRONOTIFIED.getStatus());
    List<MasCollectionStatus> response = Collections.singletonList(status);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/pcOrderExam",
      produces = {MediaType.TEXT_PLAIN_VALUE})
  ResponseEntity<String> postExam(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @RequestBody
          MasOrderExamRequest request) {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/token",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<MasTokenResponse> postForToken() {
    MasTokenResponse response = apiService.getToken();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
