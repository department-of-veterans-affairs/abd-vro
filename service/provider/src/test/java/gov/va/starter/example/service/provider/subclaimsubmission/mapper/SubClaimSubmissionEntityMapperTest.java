package gov.va.starter.example.service.provider.subclaimsubmission.mapper;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.claimsubmission.factory.ClaimSubmissionFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import gov.va.starter.example.persistence.model.SubClaimSubmissionEntity;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
import gov.va.starter.example.subclaimsubmission.factory.SubClaimSubmissionFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SubClaimSubmissionEntityMapperTest {

  private SubClaimSubmissionFactory resourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionData resourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
  private ClaimSubmissionFactory parentFactory = new ClaimSubmissionFactory();
  private ClaimSubmissionData parentData = parentFactory.createBySpec(DEFAULT_SPEC);
  private SubClaimSubmissionEntityMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(SubClaimSubmissionEntityMapper.class);
  }

  @Test
  public void mapperNewResourceTest() {
    SubClaimSubmission resource = createResource(null);

    SubClaimSubmissionEntity response = mapper.toEntity(resource);

    verifyResourceEntity(response, false, false);
  }

  @Test
  public void mapperResourceTest() {
    SubClaimSubmission resource = createResource(resourceData.getId());

    SubClaimSubmissionEntity response = mapper.toEntity(resource);

    verifyResourceEntity(response, true, false);
  }

  @Test
  public void mapperEntityTest() {
    SubClaimSubmissionEntity entity = createResourceEntity();

    SubClaimSubmission response = mapper.toModel(entity);

    verifyResource(response);
  }

  @Test
  public void mapperOptionalEntityTest() {
    Optional<SubClaimSubmissionEntity> entity = Optional.of(createResourceEntity());

    Optional<SubClaimSubmission> response = mapper.toModel(entity);

    assertThat(response.isPresent());
    verifyResource(response.get());
  }

  @Test
  public void mapperOptionalTest() {
    Optional<SubClaimSubmission> resource = Optional.of(createResource(null));

    Optional<SubClaimSubmissionEntity> response = mapper.toEntity(resource);

    assertThat(response.isPresent());
    verifyResourceEntity(response.get(), false, false);
  }

  @Test
  public void mapperOptionalNullTest() {
    Optional<SubClaimSubmission> resource = Optional.ofNullable(null);

    Optional<SubClaimSubmissionEntity> response = mapper.toEntity(resource);

    assertThat(response.isEmpty());
  }

  @Test
  public void mapperOptionalEmptyTest() {
    Optional<SubClaimSubmission> resource = Optional.empty();

    Optional<SubClaimSubmissionEntity> response = mapper.toEntity(resource);

    assertThat(response.isEmpty());
  }

  @Test
  public void mapperEntityListTest() {
    List<SubClaimSubmissionEntity> entities =
        Arrays.asList(createResourceEntity(), createResourceEntity());

    List<SubClaimSubmission> response = mapper.toModelList(entities);

    assertThat(response.size()).isEqualTo(2);
    verifyResource(response.get(0));
    verifyResource(response.get(1));
  }

  @Test
  public void mapperEntityPageTest() {
    Pageable pageable = PageRequest.of(0, 3);
    Page<SubClaimSubmissionEntity> entities =
        new PageImpl<>(
            Arrays.asList(createResourceEntity(), createResourceEntity(), createResourceEntity()),
            pageable,
            100);

    Page<SubClaimSubmission> response = mapper.toModelPage(entities);

    assertThat(response.getContent().size()).isEqualTo(3);
    assertThat(response.getTotalElements()).isEqualTo(100);
    assertThat(response.getNumber()).isEqualTo(0);
    assertThat(response.getNumberOfElements()).isEqualTo(3);

    verifyResource(response.toList().get(0));
    verifyResource(response.toList().get(1));
    verifyResource(response.toList().get(2));
  }

  /**
   * convenience function to create resource object.
   *
   * @param id whether to create with identifier (null if not)
   * @return SubClaimSubmission object
   */
  private SubClaimSubmission createResource(String id) {
    return new SubClaimSubmission(
        id,
        resourceData.getUserName(),
        resourceData.getPii(),
        resourceData.getFirstName(),
        resourceData.getLastName());
  }

  /**
   * convenience function to create resource entity object.
   *
   * @return SubClaimSubmissionEntity object
   */
  private SubClaimSubmissionEntity createResourceEntity() {
    return new SubClaimSubmissionEntity(
        resourceData.getId(),
        resourceData.getUserName(),
        resourceData.getPii(),
        resourceData.getFirstName(),
        resourceData.getLastName(),
        parentData.getId());
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  protected void verifyResource(SubClaimSubmission response) {
    assertThat(response.getUserName()).isEqualTo(resourceData.getUserName());
    assertThat(response.getPii()).isEqualTo(resourceData.getPii());
    assertThat(response.getFirstName()).isEqualTo(resourceData.getFirstName());
    assertThat(response.getLastName()).isEqualTo(resourceData.getLastName());
    assertThat(response.getId()).isEqualTo(resourceData.getId());
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  private void verifyResourceEntity(SubClaimSubmissionEntity response) {
    verifyResourceEntity(response, true, true);
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  // CSOFF: LineLength
  private void verifyResourceEntity(
      SubClaimSubmissionEntity response, boolean hasId, boolean hasParentId) {
    // CSON: LineLength
    assertThat(response.getUserName()).isEqualTo(resourceData.getUserName());
    assertThat(response.getPii()).isEqualTo(resourceData.getPii());
    assertThat(response.getFirstName()).isEqualTo(resourceData.getFirstName());
    assertThat(response.getLastName()).isEqualTo(resourceData.getLastName());
    if (hasParentId) {
      assertThat(response.getClaimSubmissionId()).isEqualTo(parentData.getId());
    } else {
      assertThat(response.getClaimSubmissionId()).isNotEqualTo(parentData.getId());
    }
    if (hasId) {
      assertThat(response.getId()).isEqualTo(resourceData.getId());
    } else {
      assertThat(response.getId()).isNotEqualTo(resourceData.getId());
    }
  }
}
