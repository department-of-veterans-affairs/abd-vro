package gov.va.starter.example.controller.claimsubmission;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.boot.notifier.lifecycle.entity.spi.EntityLifecycleNotifier;
import gov.va.starter.example.api.claimsubmission.requests.ClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.resources.ClaimSubmissionResource;
import gov.va.starter.example.api.claimsubmission.responses.ClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.controller.claimsubmission.mapper.ClaimSubmissionRequestMapper;
import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import gov.va.vro.service.provider.CamelEntrance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RestController
public class ClaimSubmissionController implements ClaimSubmissionResource {

  // https://www.baeldung.com/constructor-injection-in-spring#implicit-constructor-injection
  private final CamelEntrance camelEntrance;
  private final ClaimSubmissionService manager;
  private final ClaimSubmissionRequestMapper mapper;
  private final EntityLifecycleNotifier notifier;
  // TODO: Need to find a better way to determine version of entity
  private final String entityVersion = "0.0.1";

  /**
   * constructor.
   *
   * @param manager instance of ClaimSubmission manager
   * @param mapper instance of ClaimSubmission request mappper
   */
  public ClaimSubmissionController(
      CamelEntrance camelEntrance,
      ClaimSubmissionService manager,
      ClaimSubmissionRequestMapper mapper,
      EntityLifecycleNotifier notifier) {
    this.camelEntrance = camelEntrance;
    this.manager = manager;
    this.mapper = mapper;
    this.notifier = notifier;
  }

  private boolean useCamel = true;

  ClaimSubmission handlePost(ClaimSubmission resource) {
    return useCamel ? camelEntrance.postClaim(resource) : manager.add(resource);
  }

  @Override
  public ResponseEntity<ClaimSubmissionResponse> addEntity(ClaimSubmissionRequest addEntityRequest)
      throws RequestValidationException {

    log.info("username->{}", addEntityRequest.getUserName());
    ClaimSubmission resource = mapper.toModel(addEntityRequest);
    ClaimSubmission saved = handlePost(resource);

    ClaimSubmissionResponse response = mapper.toClaimSubmissionResponse(saved);
    notifier.created(saved, entityVersion, URI.create("user:anonymous"));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<ClaimSubmissionResponse> findEntityById(String id)
      throws ResourceNotFoundException {

    log.info("id->{}", id);
    Optional<ClaimSubmission> found = manager.findById(id);
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<PagedResponse<ClaimSubmissionResponse>> findEntities(Pageable pageable) {
    Page<ClaimSubmission> resources = manager.findAll(pageable);

    return new ResponseEntity<>(mapper.toClaimSubmissionResponsePage(resources), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimSubmissionResponse> updateEntityById(
      String id, ClaimSubmissionRequest request)
      throws ResourceNotFoundException, RequestValidationException {

    log.info("id->{}", id);
    Optional<ClaimSubmission> found = manager.updateById(id, mapper.toModel(request));
    if (found.isPresent()) {
      notifier.updated(found.get(), entityVersion, URI.create("user:anonymous"));
    }
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimSubmissionResponse> deleteEntityById(String id)
      throws ResourceNotFoundException {

    log.info("id->{}", id);
    Optional<ClaimSubmission> found = manager.deleteById(id);
    if (found.isPresent()) {
      notifier.deleted(found.get(), entityVersion, URI.create("user:anonymous"));
    }
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }

  @Override
  // CSOFF: LineLength
  public ResponseEntity<SubClaimSubmissionResponse> addSubClaimSubmission(
      String id, SubClaimSubmissionRequest addEntityRequest)
      // CSON: LineLength
      throws RequestValidationException {

    log.info("username->{}", addEntityRequest.getUserName());
    SubClaimSubmission resource = mapper.toModel(addEntityRequest);
    SubClaimSubmission saved = manager.addSubClaimSubmission(id, resource);
    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(saved);
    notifier.created(saved, entityVersion, URI.create("user:anonymous"));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  // CSOFF: LineLength
  public ResponseEntity<SubClaimSubmissionResponse> getSubClaimSubmission(
      String id, String subResourceId)
      // CSON: LineLength
      throws ResourceNotFoundException {

    log.info("id->{} subresource->{}", id, subResourceId);
    Optional<SubClaimSubmission> found = manager.getSubClaimSubmission(id, subResourceId);
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toSubClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }

  @Override
  // CSOFF: LineLength
  public ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> getSubClaimSubmissions(
      String id, Pageable pageable) {
    // CSON: LineLength
    Page<SubClaimSubmission> resources = manager.getSubClaimSubmissions(id, pageable);

    return new ResponseEntity<>(mapper.toSubClaimSubmissionResponsePage(resources), HttpStatus.OK);
  }

  @Override
  // CSOFF: LineLength
  public ResponseEntity<SubClaimSubmissionResponse> updateSubClaimSubmission(
      String id, String subResourceId, SubClaimSubmissionRequest request)
      // CSON: LineLength
      throws ResourceNotFoundException, RequestValidationException {

    log.info("id->{}", id);
    Optional<SubClaimSubmission> found =
        manager.updateSubClaimSubmission(id, subResourceId, mapper.toModel(request));
    if (found.isPresent()) {
      notifier.updated(found.get(), entityVersion, URI.create("user:anonymous"));
    }
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toSubClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }

  @Override
  // CSOFF: LineLength
  public ResponseEntity<SubClaimSubmissionResponse> deleteSubClaimSubmission(
      String id, String subResourceId)
      // CSON: LineLength
      throws ResourceNotFoundException {

    log.info("id->{}", id);
    Optional<SubClaimSubmission> found = manager.deleteSubClaimSubmission(id, subResourceId);
    if (found.isPresent()) {
      notifier.deleted(found.get(), entityVersion, URI.create("user:anonymous"));
    }
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toSubClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }
}
