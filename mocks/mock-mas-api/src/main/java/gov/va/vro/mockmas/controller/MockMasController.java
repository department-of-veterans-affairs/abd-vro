package gov.va.vro.mockmas.controller;

import gov.va.vro.mockmas.config.MasApiService;
import gov.va.vro.mockmas.model.CollectionStore;
import gov.va.vro.mockmas.model.ConditionInfo;
import gov.va.vro.mockmas.model.ExamOrderStore;
import gov.va.vro.mockmas.model.MasTokenResponse;
import gov.va.vro.mockmas.model.OrderExamCheckResponse;
import gov.va.vro.mockmas.model.OrderExamResponse;
import gov.va.vro.mockmas.model.OrderExamSuccess;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

  private final ExamOrderStore examOrderStore;

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

    if (collectionId == 369) { // Used to test mas exceptions
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mas exception testing");
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
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<OrderExamResponse> postExam(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @RequestBody
          MasOrderExamRequest request) {
    log.info("Ordering exam for {}.", request.getCollectionsId());

    // Test cases that require a mas ERROR on ordering exam
    if (request.getCollectionsId() == 391) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Returning 500 for testing");
    }
    ConditionInfo conditionInfo = new ConditionInfo("HYPERTENSION", "HYPERTENSION");

    OrderExamSuccess success = new OrderExamSuccess();
    success.setCollectionsId(request.getCollectionsId());
    success.setConditions(Collections.singletonList(conditionInfo));

    OrderExamResponse response = new OrderExamResponse(success);
    examOrderStore.put(request.getCollectionsId(), true);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * This service does not exist in the real MAS Service. It is used for end to end testing with
   * this mock only to ensure that the /pcOrderExam path was called correctly. *
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/checkExamOrdered/{collectionsId}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<OrderExamCheckResponse> checkExamOrdered(
      @Parameter(
              name = "collectionsId",
              description = "The collectionId given to /pcOrderExam",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("collectionsId")
          Integer collectionsId) {
    log.info("Checking if exam ordered for {}.", collectionsId);

    Boolean examOrdered = examOrderStore.get(collectionsId);
    if (examOrdered == null) {
      examOrdered = false;
    }
    OrderExamCheckResponse response = new OrderExamCheckResponse(examOrdered);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * This service does not exist in the real MAS Service. It is used for end to end testing with
   * this mock only to ensure that the /pcOrderExam path was called correctly. *
   */
  @RequestMapping(
      method = RequestMethod.DELETE,
      value = "/checkExamOrdered/{collectionsId}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<OrderExamCheckResponse> deleteExamCheck(
      @Parameter(
              name = "collectionsId",
              description = "The collectionId given to /pcOrderExam",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("collectionsId")
          Integer collectionsId) {

    Boolean examOrdered = examOrderStore.get(collectionsId);
    if (examOrdered == null) {
      // Not all test cases haver exams ordered. If it's not found just proceed instead of erroring.
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    examOrderStore.reset(collectionsId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/token",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<MasTokenResponse> postForToken(
      @RequestParam MultiValueMap<String, String> paramMap) {
    log.info("Getting the token from MAS server.");
    MasTokenResponse response = apiService.getToken();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
