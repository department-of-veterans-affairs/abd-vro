package gov.va.starter.example.service.provider.claimsubmission;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.example.persistence.model.ClaimSubmissionEntityRepository;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntity;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntityRepository;
import gov.va.starter.example.service.provider.claimsubmission.mapper.ClaimSubmissionEntityMapper;
import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimStatus;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ClaimSubmissionServiceImpl implements ClaimSubmissionService {

  private ClaimSubmissionEntityRepository repository;
  private ClaimSubmissionEntityMapper mapper;
  private SubClaimSubmissionEntityRepository subResourceRepository;

  ClaimSubmissionServiceImpl(
      ClaimSubmissionEntityRepository repository,
      ClaimSubmissionEntityMapper mapper,
      SubClaimSubmissionEntityRepository subResourceRepository) {
    this.repository = repository;
    this.mapper = mapper;
    this.subResourceRepository = subResourceRepository;
  }

  /**
   * add a new ClaimSubmission entity.
   *
   * @param resource resource info to add (id should be null)
   * @return new resource object with valid id
   */
  @Override
  public ClaimSubmission add(ClaimSubmission resource) throws RequestValidationException {
    ClaimSubmission saved = mapper.toModel(repository.save(mapper.toEntity(resource)));
    return saved;
  }

  /**
   * find all resources matching last name.
   *
   * @param lastName criteria for match
   * @return list of matching ClaimSubmission records
   */
  @Override
  public Page<ClaimSubmission> findByLastName(String lastName, Pageable pageable) {
    log.info("looking up by lastname of:{}", lastName);
    Page<ClaimSubmission> responseList =
        mapper.toModelPage(repository.findByLastName(lastName, pageable));
    log.info("Response list size:{}", responseList.getContent().size());
    return responseList;
  }

  /**
   * find resource by user name.
   *
   * @param userName username criteria to match
   * @return matching record, or null
   */
  @Override
  public Optional<ClaimSubmission> findByUserName(String userName) {
    log.info("looking up by username:{}", userName);
    Optional<ClaimSubmission> resource = mapper.toModel(repository.findByUserName(userName));
    return resource;
  }

  @Override
  public Optional<ClaimSubmission> findById(String id) {
    Optional<ClaimSubmission> resource = mapper.toModel(repository.findById(id));
    return resource;
  }

  @Override
  public Page<ClaimSubmission> findAll(Pageable pageable) {
    Page<ClaimSubmission> resource = mapper.toModelPage(repository.findAll(pageable));
    return resource;
  }

  @Override
  // CSOFF: LineLength
  public Optional<ClaimSubmission> updateById(String id, ClaimSubmission record)
      // CSON: LineLength
      throws RequestValidationException {
    Optional<ClaimSubmission> resource =
        mapper.toModel(
            repository
                .findById(id)
                .map((obj) -> mapper.updateMetadata(record, obj))
                .map((obj) -> repository.save(obj)));

    return resource;
  }

  // TODO: use updateById instead?
  public Optional<ClaimSubmission> updateStatusById(String id, ClaimStatus claimStatus) {
    Optional<ClaimSubmission> resource =
        mapper.toModel(
            repository
                .findById(id)
                .map(
                    (obj) -> {
                      // TODO: fix multiple `ClaimStatus` class
                      gov.va.vro.model.ClaimStatus status =
                          gov.va.vro.model.ClaimStatus.valueOf(claimStatus.name());
                      obj.setStatus(status);
                      return repository.save(obj);
                    }));
    return resource;
  }

  @Override
  public Optional<ClaimSubmission> deleteById(String id) {
    Optional<ClaimSubmission> resource = findById(id);
    repository.deleteById(id);
    return resource;
  }

  /**
   * add a new SubClaimSubmission entity.
   *
   * @param id ClaimSubmission resource id
   * @param subResource resource info to add (id should be null)
   * @return new resource object with valid id
   */
  @Override
  // CSOFF: LineLength
  public SubClaimSubmission addSubClaimSubmission(String id, SubClaimSubmission subResource)
      // CSON: LineLength
      throws RequestValidationException {
    SubClaimSubmissionEntity entity = mapper.toSubClaimSubmissionEntity(subResource);
    entity.setClaimSubmissionId(id);
    SubClaimSubmission saved = mapper.toSubClaimSubmissionModel(subResourceRepository.save(entity));
    return saved;
  }

  /**
   * find a SubClaimSubmission resource by resource id.
   *
   * @param id ClaimSubmission resource id
   * @param subResourceId id of the SubClaimSubmission
   * @return matching record, or null
   */
  @Override
  // CSOFF: LineLength
  public Optional<SubClaimSubmission> getSubClaimSubmission(String id, String subResourceId) {
    // CSON: LineLength
    Optional<SubClaimSubmission> resource =
        mapper.toSubClaimSubmissionModel(subResourceRepository.findById(subResourceId));
    return resource;
  }

  /**
   * find all SubClaimSubmission resources related to ClaimSubmission.
   *
   * @param id ClaimSubmission resource id
   * @return list of SubClaimSubmission resources
   */
  @Override
  // CSOFF: LineLength
  public Page<SubClaimSubmission> getSubClaimSubmissions(String id, Pageable pageable) {
    // CSON: LineLength
    Page<SubClaimSubmission> resources =
        mapper.toSubClaimSubmissionModelPage(
            subResourceRepository.findAllByClaimSubmissionId(id, pageable));
    return resources;
  }

  /**
   * update a SubClaimSubmission resource based on id.
   *
   * @param id ClaimSubmission resource id
   * @param subResourceId SubClaimSubmission resource id
   * @param record SubClaimSubmission resource data
   * @return Optional<> reference to updated SubClaimSubmission resource
   */
  @Override
  // CSOFF: LineLength
  public Optional<SubClaimSubmission> updateSubClaimSubmission(
      String id, String subResourceId, SubClaimSubmission record)
      // CSON: LineLength
      throws RequestValidationException {
    Optional<SubClaimSubmission> resource =
        mapper.toSubClaimSubmissionModel(
            subResourceRepository
                .findById(subResourceId)
                .map((obj) -> mapper.updateSubClaimSubmissionMetadata(record, obj))
                .map((obj) -> subResourceRepository.save(obj)));

    return resource;
  }

  /**
   * delete a SubClaimSubmission resource based on id.
   *
   * @param id ClaimSubmission resource id
   * @param subResourceId SubClaimSubmission resource id
   * @return subResource SubClaimSubmission resource data
   */
  @Override
  // CSOFF: LineLength
  public Optional<SubClaimSubmission> deleteSubClaimSubmission(String id, String subResourceId) {
    // CSON: LineLength
    Optional<SubClaimSubmission> result = getSubClaimSubmission(id, subResourceId);
    subResourceRepository.deleteById(subResourceId);
    return result;
  }
}
