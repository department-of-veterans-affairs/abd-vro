package gov.va.starter.example.service.provider.subclaimsubmission;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;

import gov.va.starter.example.persistence.model.SubClaimSubmissionEntity;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntityRepository;
import gov.va.starter.example.service.provider.subclaimsubmission.mapper.SubClaimSubmissionEntityMapper;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
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
public class SubClaimSubmissionServiceImplTest {

  private SubClaimSubmissionServiceImpl manager;

  @Mock private SubClaimSubmissionEntityRepository repository;
  @Mock private SubClaimSubmissionEntityMapper mapper;

  private SubClaimSubmissionFactory resourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionData defaultResourceData;
  private SubClaimSubmissionData bogusResourceData;
  private SubClaimSubmission resource;
  private SubClaimSubmission output;
  private SubClaimSubmissionEntity entity;
  private SubClaimSubmissionEntity added;
  private Optional<SubClaimSubmission> emptyResource = Optional.empty();
  private Optional<SubClaimSubmissionEntity> emptyEntity = Optional.empty();
  private Optional<SubClaimSubmissionEntity> optionalEntity;
  private Optional<SubClaimSubmissionEntity> optionalAdded;
  private Optional<SubClaimSubmission> optionalOutput;
  private List<SubClaimSubmissionEntity> entityList;
  private List<SubClaimSubmission> outputList;
  private List<SubClaimSubmissionEntity> emptyEntityList = Arrays.asList();
  private List<SubClaimSubmission> emptyOutputList = Arrays.asList();
  private Page<SubClaimSubmissionEntity> entityPage;
  private Page<SubClaimSubmission> outputPage;
  private Page<SubClaimSubmissionEntity> emptyEntityPage;
  private Page<SubClaimSubmission> emptyOutputPage;
  private Pageable pageable = Pageable.unpaged();

  /** setup data for each test. */
  @BeforeEach
  public void setup() {

    manager = new SubClaimSubmissionServiceImpl(repository, mapper);

    defaultResourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
    bogusResourceData = resourceFactory.createBySpec("bogus");

    // use the real mapper to generate consistent objects to use in mapper stubs
    SubClaimSubmissionEntityMapper real = Mappers.getMapper(SubClaimSubmissionEntityMapper.class);

    resource =
        SubClaimSubmission.builder()
            .userName(defaultResourceData.getUserName())
            .pii(defaultResourceData.getPii())
            .firstName(defaultResourceData.getFirstName())
            .lastName(defaultResourceData.getLastName())
            .build();
    entity = real.toEntity(resource);
    added =
        new SubClaimSubmissionEntity(
            defaultResourceData.getId(),
            entity.getUserName(),
            entity.getPii(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getClaimSubmissionId());
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

  @Test
  public void findBySubClaimSubmissionIdFailTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(Mockito.any())).thenReturn(emptyEntity);

    Optional<SubClaimSubmission> result = manager.findById(bogusResourceData.getId());
    Assertions.assertThat(!result.isPresent()).isTrue();
  }

  @Test
  public void addSubClaimSubmissionTest() {

    createMapperStubs();
    Mockito.when(repository.save(entity)).thenReturn(added);

    SubClaimSubmission response = manager.add(resource);

    Assertions.assertThat(response.getFirstName()).isEqualTo(resource.getFirstName());
    Assertions.assertThat(response.getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByUserNameTest() {

    createOptionalMapperStubs();
    Mockito.when(repository.findByUserName(defaultResourceData.getUserName()))
        .thenReturn(optionalAdded);

    Optional<SubClaimSubmission> response =
        manager.findByUserName(defaultResourceData.getUserName());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(added.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByUserNameFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findByUserName(bogusResourceData.getUserName()))
        .thenReturn(emptyEntity);

    Optional<SubClaimSubmission> response = manager.findByUserName(bogusResourceData.getUserName());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void findByLastNameTest() {

    createListMapperStubs();
    Mockito.when(repository.findByLastName(defaultResourceData.getLastName(), pageable))
        .thenReturn(entityPage);

    Page<SubClaimSubmission> response =
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

    Page<SubClaimSubmission> response =
        manager.findByLastName(bogusResourceData.getLastName(), pageable);

    Assertions.assertThat(response.getContent().isEmpty()).isTrue();
  }

  @Test
  public void findByIdTest() {

    createOptionalMapperStubs();
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(optionalAdded);

    Optional<SubClaimSubmission> response = manager.findById(defaultResourceData.getId());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(added.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(added.getId());
  }

  @Test
  public void findByIdFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(bogusResourceData.getId())).thenReturn(emptyEntity);

    Optional<SubClaimSubmission> response = manager.findById(bogusResourceData.getId());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void findAllTest() {

    createListMapperStubs();
    Mockito.when(repository.findAll(pageable)).thenReturn(entityPage);

    Page<SubClaimSubmission> response = manager.findAll(pageable);

    Assertions.assertThat(response.getContent().size()).isEqualTo(2);
  }

  @Test
  public void findAllEmptyTest() {

    createEmptyListMapperStubs();
    Mockito.when(repository.findAll(pageable)).thenReturn(emptyEntityPage);

    Page<SubClaimSubmission> response = manager.findAll(pageable);

    Assertions.assertThat(response.getContent().size()).isEqualTo(0);
  }

  @Test
  public void updateTest() {

    createOptionalMapperStubs();
    Mockito.when(mapper.updateMetadata(resource, added)).thenReturn(added);
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(optionalAdded);
    Mockito.when(repository.save(added)).thenReturn(added);

    Optional<SubClaimSubmission> response =
        manager.updateById(defaultResourceData.getId(), resource);

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(resource.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(defaultResourceData.getId());
  }

  @Test
  public void updateFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(emptyEntity);

    Optional<SubClaimSubmission> response =
        manager.updateById(defaultResourceData.getId(), resource);

    Assertions.assertThat(response.isEmpty()).isTrue();
  }

  @Test
  public void deleteTest() {

    createOptionalMapperStubs();
    Mockito.when(repository.findById(defaultResourceData.getId())).thenReturn(optionalAdded);

    Optional<SubClaimSubmission> response = manager.deleteById(defaultResourceData.getId());

    Assertions.assertThat(response.isPresent()).isTrue();
    Assertions.assertThat(response.get().getFirstName()).isEqualTo(added.getFirstName());
    Assertions.assertThat(response.get().getId()).isEqualTo(added.getId());
  }

  @Test
  public void deleteFailedTest() {

    createEmptyMapperStubs();
    Mockito.when(repository.findById(bogusResourceData.getId())).thenReturn(emptyEntity);

    Optional<SubClaimSubmission> response = manager.deleteById(bogusResourceData.getId());

    Assertions.assertThat(response.isEmpty()).isTrue();
  }
}
