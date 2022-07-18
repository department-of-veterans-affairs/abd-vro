package gov.va.starter.example.service.provider.subclaimsubmission;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntityRepository;
import gov.va.starter.example.service.provider.subclaimsubmission.mapper.SubClaimSubmissionEntityMapper;
import gov.va.starter.example.service.spi.subclaimsubmission.SubClaimSubmissionService;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SubClaimSubmissionServiceImpl implements SubClaimSubmissionService {

  private SubClaimSubmissionEntityRepository repository;
  private SubClaimSubmissionEntityMapper mapper;

  SubClaimSubmissionServiceImpl(
      SubClaimSubmissionEntityRepository repository, SubClaimSubmissionEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  /**
   * add a new SubClaimSubmission entity.
   *
   * @param resource resource info to add (id should be null)
   * @return new resource object with valid id
   */
  @Override
  public SubClaimSubmission add(SubClaimSubmission resource) throws RequestValidationException {
    SubClaimSubmission saved = mapper.toModel(repository.save(mapper.toEntity(resource)));
    return saved;
  }

  /**
   * find all resources matching last name.
   *
   * @param lastName criteria for match
   * @return list of matching SubClaimSubmission records
   */
  @Override
  public Page<SubClaimSubmission> findByLastName(String lastName, Pageable pageable) {
    log.info("looking up by lastname of:{}", lastName);
    Page<SubClaimSubmission> responseList =
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
  public Optional<SubClaimSubmission> findByUserName(String userName) {
    log.info("looking up by username:{}", userName);
    Optional<SubClaimSubmission> resource = mapper.toModel(repository.findByUserName(userName));
    return resource;
  }

  @Override
  public Optional<SubClaimSubmission> findById(String id) {
    Optional<SubClaimSubmission> resource = mapper.toModel(repository.findById(id));
    return resource;
  }

  @Override
  public Page<SubClaimSubmission> findAll(Pageable pageable) {
    Page<SubClaimSubmission> resource = mapper.toModelPage(repository.findAll(pageable));
    return resource;
  }

  @Override
  // CSOFF: LineLength
  public Optional<SubClaimSubmission> updateById(String id, SubClaimSubmission record)
      // CSON: LineLength
      throws RequestValidationException {
    Optional<SubClaimSubmission> resource =
        mapper.toModel(
            repository
                .findById(id)
                .map((obj) -> mapper.updateMetadata(record, obj))
                .map((obj) -> repository.save(obj)));

    return resource;
  }

  @Override
  public Optional<SubClaimSubmission> deleteById(String id) {
    Optional<SubClaimSubmission> resource = findById(id);
    repository.deleteById(id);
    return resource;
  }
}
