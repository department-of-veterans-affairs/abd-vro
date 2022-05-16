package gov.va.starter.example.service.provider.claimsubmission;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;

import gov.va.starter.example.claimsubmission.factory.ClaimSubmissionFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.starter.example.persistence.model.ClaimSubmissionEntityRepository;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntity;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntityRepository;
import gov.va.starter.example.service.provider.claimsubmission.mapper.ClaimSubmissionEntityMapper;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import gov.va.starter.example.subclaimsubmission.factory.SubClaimSubmissionFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ClaimSubmissionServiceImplTest {

  private ClaimSubmissionServiceImpl manager;

  @Mock private ClaimSubmissionEntityRepository repository;
  @Mock private ClaimSubmissionEntityMapper mapper;
  @Mock private SubClaimSubmissionEntityRepository subResourceRepository;

  private ClaimSubmissionFactory resourceFactory = new ClaimSubmissionFactory();
  private ClaimSubmissionData defaultResourceData;
  private ClaimSubmissionData bogusResourceData;
  private ClaimSubmission resource;
  private ClaimSubmission output;
  private ClaimSubmissionEntity entity;
  private ClaimSubmissionEntity added;
  private Optional<ClaimSubmission> emptyResource = Optional.empty();
  private Optional<ClaimSubmissionEntity> emptyEntity = Optional.empty();
  private Optional<ClaimSubmissionEntity> optionalEntity;
  private Optional<ClaimSubmissionEntity> optionalAdded;
  private Optional<ClaimSubmission> optionalOutput;
  private List<ClaimSubmissionEntity> entityList;
  private List<ClaimSubmission> outputList;
  private List<ClaimSubmissionEntity> emptyEntityList = Arrays.asList();
  private List<ClaimSubmission> emptyOutputList = Arrays.asList();
  private Page<ClaimSubmissionEntity> entityPage;
  private Page<ClaimSubmission> outputPage;
  private Page<ClaimSubmissionEntity> emptyEntityPage;
  private Page<ClaimSubmission> emptyOutputPage;
  private Pageable pageable = Pageable.unpaged();

  private SubClaimSubmissionFactory subResourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionData defaultSubResourceData;
  private SubClaimSubmission subResource;
  private SubClaimSubmission subOutput;
  private SubClaimSubmissionEntity subEntity;
  private SubClaimSubmissionEntity subAdded;
  private Optional<SubClaimSubmission> emptySubResource = Optional.empty();
  private Optional<SubClaimSubmissionEntity> emptySubEntity = Optional.empty();
  private Optional<SubClaimSubmissionEntity> optionalSubEntity;
  private Optional<SubClaimSubmissionEntity> optionalSubAdded;
  private Optional<SubClaimSubmission> optionalSubOutput;
  private List<SubClaimSubmissionEntity> subEntityList;
  private List<SubClaimSubmission> subOutputList;
  private List<SubClaimSubmissionEntity> emptySubEntityList = Arrays.asList();
  private List<SubClaimSubmission> emptySubOutputList = Arrays.asList();
  private Page<SubClaimSubmissionEntity> subEntityPage;
  private Page<SubClaimSubmission> subOutputPage;
  private Page<SubClaimSubmissionEntity> emptySubEntityPage;
  private Page<SubClaimSubmission> emptySubOutputPage;

  /** setup data for each test. */
  @BeforeEach
  public void setup() {

    manager = new ClaimSubmissionServiceImpl(repository, mapper, subResourceRepository);

    defaultResourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
    bogusResourceData = resourceFactory.createBySpec("bogus");
    defaultSubResourceData = subResourceFactory.createBySpec(DEFAULT_SPEC);

    // use the real mapper to generate consistent objects to use in mapper stubs
    ClaimSubmissionEntityMapper real = Mappers.getMapper(ClaimSubmissionEntityMapper.class);

    resource =
        ClaimSubmission.builder()
            .userName(defaultResourceData.getUserName())
            .pii(defaultResourceData.getPii())
            .firstName(defaultResourceData.getFirstName())
            .lastName(defaultResourceData.getLastName())
            .build();
    entity = real.toEntity(resource);
    added =
        new ClaimSubmissionEntity(
            defaultResourceData.getId(),
            entity.getUserName(),
            entity.getPii(),
            entity.getFirstName(),
            entity.getLastName());
    output = real.toModel(added);
    optionalEntity = Optional.of(entity);
    optionalAdded = Optional.of(added);
    optionalOutput = Optional.of(output);
    entityList = Arrays.asList(added, added);
    outputList = Arrays.asList(output, output);
    entityPage = new PageImpl<>(entityList);
    outputPage = new PageImpl<>(outputList);
    emptyEntityPage = new PageImpl<>(emptyEntityList);
    emptyOutputPage = new PageImpl<>(emptyOutputList);

    subResource =
        SubClaimSubmission.builder()
            .userName(defaultSubResourceData.getUserName())
            .firstName(defaultSubResourceData.getFirstName())
            .lastName(defaultSubResourceData.getLastName())
            .build();
    subEntity = real.toSubClaimSubmissionEntity(subResource);
    subAdded =
        new SubClaimSubmissionEntity(
            defaultSubResourceData.getId(),
            subEntity.getUserName(),
            subEntity.getPii(),
            subEntity.getFirstName(),
            subEntity.getLastName(),
            defaultResourceData.getId());
    subOutput = real.toSubClaimSubmissionModel(subAdded);
    optionalSubEntity = Optional.of(subEntity);
    optionalSubAdded = Optional.of(subAdded);
    optionalSubOutput = Optional.of(subOutput);
    subEntityList = Arrays.asList(subAdded, subAdded);
    subOutputList = Arrays.asList(subOutput, subOutput);
    subEntityPage = new PageImpl<>(subEntityList);
    subOutputPage = new PageImpl<>(subOutputList);
    emptySubEntityPage = new PageImpl<>(emptySubEntityList);
    emptySubOutputPage = new PageImpl<>(emptySubOutputList);
  }

  private void createMapperStubs() {
    Mockito.when(mapper.toEntity(resource)).thenReturn(entity);
    Mockito.when(mapper.toModel(added)).thenReturn(output);
  }

  private void createOptionalMapperStubs() {
    Mockito.when(mapper.toModel(optionalAdded)).thenReturn(optionalOutput);
  }

  private void createEmptyMapperStubs() {
    Mockito.when(mapper.toModel(emptyEntity)).thenReturn(emptyResource);
  }

  private void createListMapperStubs() {
    Mockito.when(mapper.toModelPage(entityPage)).thenReturn(outputPage);
  }

  private void createEmptyListMapperStubs() {
    Mockito.when(mapper.toModelPage(emptyEntityPage)).thenReturn(emptyOutputPage);
  }

  private void createSubClaimSubmissionMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionEntity(subResource)).thenReturn(subEntity);
    Mockito.when(mapper.toSubClaimSubmissionModel(subAdded)).thenReturn(subOutput);
  }

  private void createOptionalSubClaimSubmissionMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionModel(optionalSubAdded)).thenReturn(optionalSubOutput);
  }

  private void createEmptySubClaimSubmissionMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionModel(emptySubEntity)).thenReturn(emptySubResource);
  }

  private void createSubClaimSubmissionListMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionModelPage(subEntityPage)).thenReturn(subOutputPage);
  }

  private void createEmptySubClaimSubmissionListMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionModelPage(emptySubEntityPage))
        .thenReturn(emptySubOutputPage);
  }

  @Test
  public void findByClaimSubmissionIdFailTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(Mockito.any())).thenReturn(emptyEntity);

    Optional<ClaimSubmission> result = manager.findById(bogusResourceData.getId());
    Assertions.assertThat(!result.isPresent()).isTrue();
  }

  @Test
  public void addClaimSubmissionTest() {

    createMapperStubs();
    Mockito.when(repository.save(entity)).thenReturn(added);

    ClaimSubmission response = manager.add(resource);

    Assertions.assertThat(response.getFirstName()).isEqualTo(resource.getFirstName());
    Assertions.assertThat(response.getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByUserNameTest() {

    createOptionalMapperStubs();
    Mockito.when(repository.findByUserName(defaultResourceData.getUserName()))
        .thenReturn(optionalAdded);

    Optional<ClaimSubmission> response = manager.findByUserName(defaultResourceData.getUserName());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(added.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByUserNameFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findByUserName(bogusResourceData.getUserName()))
        .thenReturn(emptyEntity);

    Optional<ClaimSubmission> response = manager.findByUserName(bogusResourceData.getUserName());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void findByLastNameTest() {

    createListMapperStubs();
    Mockito.when(repository.findByLastName(defaultResourceData.getLastName(), pageable))
        .thenReturn(entityPage);

    Page<ClaimSubmission> response =
        manager.findByLastName(defaultResourceData.getLastName(), pageable);

    Assertions.assertThat(response.getContent().isEmpty()).isFalse();
    Assertions.assertThat(response.getContent().get(0).getFirstName())
        .isEqualTo(added.getFirstName());
    Assertions.assertThat(response.getContent().get(0).getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByLastNameFailedTest() {

    createEmptyListMapperStubs();
    Mockito.when(repository.findByLastName(bogusResourceData.getLastName(), pageable))
        .thenReturn(emptyEntityPage);

    Page<ClaimSubmission> response =
        manager.findByLastName(bogusResourceData.getLastName(), pageable);

    Assertions.assertThat(response.getContent().isEmpty()).isTrue();
  }

  @Test
  public void findByIdTest() {

    createOptionalMapperStubs();
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(optionalAdded);

    Optional<ClaimSubmission> response = manager.findById(defaultResourceData.getId());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(added.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByIdFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(bogusResourceData.getId())).thenReturn(emptyEntity);

    Optional<ClaimSubmission> response = manager.findById(bogusResourceData.getId());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void findAllTest() {

    createListMapperStubs();
    Mockito.when(repository.findAll(pageable)).thenReturn(entityPage);

    Page<ClaimSubmission> response = manager.findAll(pageable);

    Assertions.assertThat(response.getContent().size()).isEqualTo(2);
  }

  @Test
  public void findAllEmptyTest() {

    createEmptyListMapperStubs();
    Mockito.when(repository.findAll(pageable)).thenReturn(emptyEntityPage);

    Page<ClaimSubmission> response = manager.findAll(pageable);

    Assertions.assertThat(response.getContent().size()).isEqualTo(0);
  }

  @Test
  public void updateTest() {

    createOptionalMapperStubs();
    Mockito.when(mapper.updateMetadata(resource, added)).thenReturn(added);
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(optionalAdded);
    Mockito.when(repository.save(added)).thenReturn(added);

    Optional<ClaimSubmission> response = manager.updateById(defaultResourceData.getId(), resource);

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(resource.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(defaultResourceData.getId());
  }

  @Test
  public void updateFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(emptyEntity);

    Optional<ClaimSubmission> response = manager.updateById(defaultResourceData.getId(), resource);

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void deleteTest() {

    createOptionalMapperStubs();
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(optionalAdded);

    Optional<ClaimSubmission> response = manager.deleteById(defaultResourceData.getId());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(added.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(added.getId());
  }

  @Test
  public void deleteFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(bogusResourceData.getId())).thenReturn(emptyEntity);

    Optional<ClaimSubmission> response = manager.deleteById(bogusResourceData.getId());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void findBySubClaimSubmissionIdFailTest() {

    createEmptySubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.findById(Mockito.any())).thenReturn(emptySubEntity);

    Optional<SubClaimSubmission> result =
        manager.getSubClaimSubmission(defaultResourceData.getId(), "bogus");
    Assertions.assertThat(!result.isPresent()).isTrue();
  }

  @Test
  public void addSubClaimSubmissionTest() {

    createSubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.save(subEntity)).thenReturn(subAdded);

    SubClaimSubmission response =
        manager.addSubClaimSubmission(defaultResourceData.getId(), subResource);

    Assertions.assertThat(response.getFirstName()).isEqualTo(subResource.getFirstName());
    Assertions.assertThat(response.getId()).isEqualTo(subAdded.getId());
    Assertions.assertThat(response.getId()).isEqualTo(defaultSubResourceData.getId());
  }

  @Test
  public void findSubClaimSubmissionByIdTest() {

    createOptionalSubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.findById(defaultSubResourceData.getId()))
        .thenReturn(optionalSubAdded);

    Optional<SubClaimSubmission> response =
        manager.getSubClaimSubmission(defaultResourceData.getId(), defaultSubResourceData.getId());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(subAdded.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(subAdded.getId());
  }

  @Test
  public void findSubClaimSubmissionByIdFailedTest() {

    createEmptySubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.findById(bogusResourceData.getId()))
        .thenReturn(emptySubEntity);

    Optional<SubClaimSubmission> response =
        manager.getSubClaimSubmission(defaultResourceData.getId(), bogusResourceData.getId());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void findAllSubClaimSubmissionTest() {

    createSubClaimSubmissionListMapperStubs();
    Mockito.when(
            subResourceRepository.findAllByClaimSubmissionId(defaultResourceData.getId(), pageable))
        .thenReturn(subEntityPage);

    Page<SubClaimSubmission> response =
        manager.getSubClaimSubmissions(defaultResourceData.getId(), pageable);

    Assertions.assertThat(response.getContent().size()).isEqualTo(2);
  }

  @Test
  public void findAllSubClaimSubmissionEmptyTest() {

    createEmptySubClaimSubmissionListMapperStubs();
    Mockito.when(
            subResourceRepository.findAllByClaimSubmissionId(defaultResourceData.getId(), pageable))
        .thenReturn(emptySubEntityPage);

    Page<SubClaimSubmission> response =
        manager.getSubClaimSubmissions(defaultResourceData.getId(), pageable);

    Assertions.assertThat(response.getContent().size()).isEqualTo(0);
  }

  @Test
  public void updateSubClaimSubmissionTest() {

    createOptionalSubClaimSubmissionMapperStubs();
    Mockito.when(mapper.updateSubClaimSubmissionMetadata(subResource, subAdded))
        .thenReturn(subAdded);
    Mockito.when(subResourceRepository.findById(defaultSubResourceData.getId()))
        .thenReturn(optionalSubAdded);
    Mockito.when(subResourceRepository.save(subAdded)).thenReturn(subAdded);

    Optional<SubClaimSubmission> response =
        manager.updateSubClaimSubmission(
            defaultResourceData.getId(), defaultSubResourceData.getId(), subResource);

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(subResource.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(defaultSubResourceData.getId());
  }

  @Test
  public void updateSubClaimSubmissionFailedTest() {

    createEmptySubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.findById(defaultSubResourceData.getId()))
        .thenReturn(emptySubEntity);

    Optional<SubClaimSubmission> response =
        manager.updateSubClaimSubmission(
            defaultResourceData.getId(), defaultSubResourceData.getId(), subResource);

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void deleteSubClaimSubmissionTest() {

    createOptionalSubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.findById(defaultSubResourceData.getId()))
        .thenReturn(optionalSubAdded);

    Optional<SubClaimSubmission> response =
        manager.deleteSubClaimSubmission(
            defaultResourceData.getId(), defaultSubResourceData.getId());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(subAdded.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(subAdded.getId());
  }

  @Test
  public void deleteSubClaimSubmissionFailedTest() {

    createEmptySubClaimSubmissionMapperStubs();
    Mockito.when(subResourceRepository.findById(bogusResourceData.getId()))
        .thenReturn(emptySubEntity);

    Optional<SubClaimSubmission> response =
        manager.deleteSubClaimSubmission(defaultResourceData.getId(), bogusResourceData.getId());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }
}
