package gov.va.vro.mockmas.controller;

import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasCollectionStatus;
import gov.va.vro.model.mas.MasStatus;
import gov.va.vro.model.mas.request.MasCollectionAnnotationRequest;
import gov.va.vro.model.mas.request.MasCollectionStatusRequest;
import gov.va.vro.model.mas.request.MasOrderExamRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
public class MockMasController {
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/pcQueryCollectionAnnots",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<List<MasCollectionAnnotation>> getAnnotations(
      @RequestBody MasCollectionAnnotationRequest request) {
    return null;
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
      produces = { MediaType.TEXT_PLAIN_VALUE } )
  ResponseEntity<String> postExam(
      @Parameter(
          name = "claimId",
          description = "The CorpDB BNFT_CLAIM_ID",
          required = true,
          in = ParameterIn.PATH)
      @RequestBody MasOrderExamRequest request) {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }
}
