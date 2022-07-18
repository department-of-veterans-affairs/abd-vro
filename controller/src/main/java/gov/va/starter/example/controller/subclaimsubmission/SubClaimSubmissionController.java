package gov.va.starter.example.controller.subclaimsubmission;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.boot.notifier.lifecycle.entity.spi.EntityLifecycleNotifier;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.api.subclaimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.subclaimsubmission.resources.SubClaimSubmissionResource;
import gov.va.starter.example.api.subclaimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.controller.subclaimsubmission.mapper.SubClaimSubmissionRequestMapper;
import gov.va.starter.example.service.spi.subclaimsubmission.SubClaimSubmissionService;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
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
public class SubClaimSubmissionController implements SubClaimSubmissionResource {

  private final SubClaimSubmissionService manager;
  private final SubClaimSubmissionRequestMapper mapper;
  private final EntityLifecycleNotifier notifier;
  // TODO: Need to find a better way to determine version of entity
  private final String entityVersion = "0.0.1";

  /**
   * constructor.
   *
   * @param manager instance of SubClaimSubmission manager
   * @param mapper instance of SubClaimSubmission request mappper
   */
  public SubClaimSubmissionController(
      SubClaimSubmissionService manager,
      SubClaimSubmissionRequestMapper mapper,
      EntityLifecycleNotifier notifier) {
    this.manager = manager;
    this.mapper = mapper;
    this.notifier = notifier;
  }

  @Override
  public ResponseEntity<SubClaimSubmissionResponse> addEntity(
      SubClaimSubmissionRequest addEntityRequest) throws RequestValidationException {

    log.info("username->{}", addEntityRequest.getUserName());
    SubClaimSubmission resource = mapper.toModel(addEntityRequest);
    SubClaimSubmission saved = manager.add(resource);
    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(saved);
    notifier.created(saved, entityVersion, URI.create("user:anonymous"));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<SubClaimSubmissionResponse> findEntityById(String id)
      throws ResourceNotFoundException {

    log.info("id->{}", id);
    Optional<SubClaimSubmission> found = manager.findById(id);
    return new ResponseEntity<>(
        found
            .map(r -> mapper.toSubClaimSubmissionResponse(r))
            .orElseThrow(() -> new ResourceNotFoundException(id)),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> findEntities(Pageable pageable) {
    Page<SubClaimSubmission> resources = manager.findAll(pageable);

    return new ResponseEntity<>(mapper.toSubClaimSubmissionResponsePage(resources), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<SubClaimSubmissionResponse> updateEntityById(
      String id, SubClaimSubmissionRequest request)
      throws ResourceNotFoundException, RequestValidationException {

    log.info("id->{}", id);
    Optional<SubClaimSubmission> found = manager.updateById(id, mapper.toModel(request));
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
  public ResponseEntity<SubClaimSubmissionResponse> deleteEntityById(String id)
      throws ResourceNotFoundException {

    log.info("id->{}", id);
    Optional<SubClaimSubmission> found = manager.deleteById(id);
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
