package gov.va.starter.example.service.provider.claimsubmission.mapper;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.claimsubmission.factory.ClaimSubmissionFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
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

public class ClaimSubmissionEntityMapperTest {

  private ClaimSubmissionFactory resourceFactory = new ClaimSubmissionFactory();
  private ClaimSubmissionData resourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
  private ClaimSubmissionEntityMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(ClaimSubmissionEntityMapper.class);
  }

  @Test
  public void mapperNewResourceTest() {
    ClaimSubmission resource = createResource(null);

    ClaimSubmissionEntity response = mapper.toEntity(resource);

    verifyResourceEntity(response, false);
  }

  @Test
  public void mapperResourceTest() {
    ClaimSubmission resource = createResource(resourceData.getId());

    ClaimSubmissionEntity response = mapper.toEntity(resource);

    verifyResourceEntity(response);
  }

  @Test
  public void mapperEntityTest() {
    ClaimSubmissionEntity entity = createResourceEntity();

    ClaimSubmission response = mapper.toModel(entity);

    verifyResource(response);
  }

  @Test
  public void mapperOptionalEntityTest() {
    Optional<ClaimSubmissionEntity> entity = Optional.of(createResourceEntity());

    Optional<ClaimSubmission> response = mapper.toModel(entity);

    assertThat(response.isPresent());
    verifyResource(response.get());
  }

  @Test
  public void mapperOptionalTest() {
    Optional<ClaimSubmission> resource = Optional.of(createResource(null));

    Optional<ClaimSubmissionEntity> response = mapper.toEntity(resource);

    assertThat(response.isPresent());
    verifyResourceEntity(response.get(), false);
  }

  @Test
  public void mapperOptionalNullTest() {
    Optional<ClaimSubmission> resource = Optional.ofNullable(null);

    Optional<ClaimSubmissionEntity> response = mapper.toEntity(resource);

    assertThat(response.isEmpty());
  }

  @Test
  public void mapperOptionalEmptyTest() {
    Optional<ClaimSubmission> resource = Optional.empty();

    Optional<ClaimSubmissionEntity> response = mapper.toEntity(resource);

    assertThat(response.isEmpty());
  }

  @Test
  public void mapperEntityListTest() {
    List<ClaimSubmissionEntity> entities =
        Arrays.asList(createResourceEntity(), createResourceEntity());

    List<ClaimSubmission> response = mapper.toModelList(entities);

    assertThat(response.size()).isEqualTo(2);
    verifyResource(response.get(0));
    verifyResource(response.get(1));
  }

  @Test
  public void mapperEntityPageTest() {
    Pageable pageable = PageRequest.of(0, 3);
    Page<ClaimSubmissionEntity> entities =
        new PageImpl<>(
            Arrays.asList(createResourceEntity(), createResourceEntity(), createResourceEntity()),
            pageable,
            100);

    Page<ClaimSubmission> response = mapper.toModelPage(entities);

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
   * @return ClaimSubmission object
   */
  private ClaimSubmission createResource(String id) {
    return new ClaimSubmission(
        id,
        resourceData.getUserName(),
        resourceData.getPii(),
        resourceData.getFirstName(),
        resourceData.getLastName(),
        resourceData.getSubmissionId(),
        resourceData.getClaimantId(),
        resourceData.getContentionType(),
        ClaimSubmission.ClaimStatus.CREATED);
  }

  /**
   * convenience function to create resource entity object.
   *
   * @return ClaimSubmissionEntity object
   */
  private ClaimSubmissionEntity createResourceEntity() {
    return new ClaimSubmissionEntity(
        resourceData.getId(),
        resourceData.getUserName(),
        resourceData.getPii(),
        resourceData.getFirstName(),
        resourceData.getLastName(),
        resourceData.getSubmissionId(),
        resourceData.getClaimantId(),
        resourceData.getContentionType(),
        ClaimSubmissionEntity.ClaimStatus.CREATED);
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  protected void verifyResource(ClaimSubmission response) {
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
  private void verifyResourceEntity(ClaimSubmissionEntity response) {
    verifyResourceEntity(response, true);
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  // CSOFF: LineLength
  private void verifyResourceEntity(ClaimSubmissionEntity response, boolean hasId) {
    // CSON: LineLength
    assertThat(response.getUserName()).isEqualTo(resourceData.getUserName());
    assertThat(response.getPii()).isEqualTo(resourceData.getPii());
    assertThat(response.getFirstName()).isEqualTo(resourceData.getFirstName());
    assertThat(response.getLastName()).isEqualTo(resourceData.getLastName());
    if (hasId) {
      assertThat(response.getId()).isEqualTo(resourceData.getId());
    } else {
      assertThat(response.getId()).isNotEqualTo(resourceData.getId());
    }
  }
}
