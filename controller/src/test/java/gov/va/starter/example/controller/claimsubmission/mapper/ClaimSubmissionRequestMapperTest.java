package gov.va.starter.example.controller.claimsubmission.mapper;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.api.claimsubmission.requests.ClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.responses.ClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.claimsubmission.factory.ClaimSubmissionFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
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

public class ClaimSubmissionRequestMapperTest {

  private ClaimSubmissionRequestMapper mapper;
  private ClaimSubmissionFactory resourceFactory = new ClaimSubmissionFactory();
  private ClaimSubmissionData defaultResourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
  private SubClaimSubmissionFactory subResourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionData defaultSubResourceData =
      subResourceFactory.createBySpec(DEFAULT_SPEC);

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(ClaimSubmissionRequestMapper.class);
  }

  @Test
  public void mapperNewResourceTest() {
    ClaimSubmissionRequest resource = createResourceRequest();

    ClaimSubmission response = mapper.toModel(resource);

    verifyResource(response);
  }

  @Test
  public void mapperResourceResponseTest() {
    ClaimSubmission resource = createResource(defaultResourceData.getId());

    ClaimSubmissionResponse response = mapper.toClaimSubmissionResponse(resource);

    verifyResourceResponse(response);
  }

  @Test
  public void mapperOptionalTest() {
    Optional<ClaimSubmission> resource = Optional.of(createResource(defaultResourceData.getId()));

    ClaimSubmissionResponse response = mapper.toClaimSubmissionResponse(resource);

    assertThat(response).isNotNull();
    verifyResourceResponse(response);
  }

  @Test
  public void mapperOptionalNullTest() {
    Optional<ClaimSubmission> resource = Optional.ofNullable(null);

    ClaimSubmissionResponse response = mapper.toClaimSubmissionResponse(resource);

    assertThat(response).isNull();
  }

  @Test
  public void mapperOptionalEmptyTest() {
    Optional<ClaimSubmission> resource = Optional.empty();

    ClaimSubmissionResponse response = mapper.toClaimSubmissionResponse(resource);

    assertThat(response).isNull();
  }

  @Test
  public void mapperEntityListTest() {
    List<ClaimSubmission> resources =
        Arrays.asList(
            createResource(defaultResourceData.getId()),
            createResource(defaultResourceData.getId()));

    List<ClaimSubmissionResponse> response = mapper.toClaimSubmissionResponseList(resources);

    assertThat(response.size()).isEqualTo(2);
    verifyResourceResponse(response.get(0));
    verifyResourceResponse(response.get(1));
  }

  @Test
  public void mapperEntityPageTest() {
    Pageable pageable = PageRequest.of(0, 1);
    Page<ClaimSubmission> resources =
        new PageImpl<>(Arrays.asList(createResource(defaultResourceData.getId())), pageable, 100);
    PagedResponse<ClaimSubmissionResponse> response =
        mapper.toClaimSubmissionResponsePage(resources);

    assertThat(response.getItems().size()).isEqualTo(1);
    assertThat(response.getTotalItems()).isEqualTo(100);
    assertThat(response.getPageNumber()).isEqualTo(0);
    assertThat(response.getPageSize()).isEqualTo(1);
    assertThat(response.getTotalPages()).isEqualTo(100);
    verifyResourceResponse(response.getItems().get(0));
  }

  @Test
  public void mapperNewSubResourceTest() {
    SubClaimSubmissionRequest resource = createSubResourceRequest();

    SubClaimSubmission response = mapper.toModel(resource);

    verifySubResource(response);
  }

  @Test
  public void mapperSubResourceResponseTest() {
    SubClaimSubmission resource = createSubResource(defaultSubResourceData.getId());

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    verifySubResourceResponse(response);
  }

  @Test
  public void mapperOptionalSubResourceTest() {
    Optional<SubClaimSubmission> resource =
        Optional.of(createSubResource(defaultSubResourceData.getId()));

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    assertThat(response).isNotNull();
    verifySubResourceResponse(response);
  }

  @Test
  public void mapperOptionalSubResourceNullTest() {
    Optional<SubClaimSubmission> resource = Optional.ofNullable(null);

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    assertThat(response).isNull();
  }

  @Test
  public void mapperOptionalSubResourceEmptyTest() {
    Optional<SubClaimSubmission> resource = Optional.empty();

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    assertThat(response).isNull();
  }

  @Test
  public void mapperSubEntityListTest() {
    List<SubClaimSubmission> resources =
        Arrays.asList(
            createSubResource(defaultSubResourceData.getId()),
            createSubResource(defaultSubResourceData.getId()));

    List<SubClaimSubmissionResponse> response = mapper.toSubClaimSubmissionResponseList(resources);

    assertThat(response.size()).isEqualTo(2);
    verifySubResourceResponse(response.get(0));
    verifySubResourceResponse(response.get(1));
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
        defaultResourceData.getUserName(),
        defaultResourceData.getPii(),
        defaultResourceData.getFirstName(),
        defaultResourceData.getLastName(),
        defaultResourceData.getSubmissionId(),
        defaultResourceData.getClaimantId(),
        defaultResourceData.getContentionType(),
        ClaimSubmission.ClaimStatus.CREATED);
  }

  /**
   * convenience function to create subresource object.
   *
   * @param id whether to create with identifier (null if not)
   * @return SubClaimSubmission object
   */
  private SubClaimSubmission createSubResource(String id) {
    return new SubClaimSubmission(
        id,
        defaultSubResourceData.getUserName(),
        defaultSubResourceData.getFirstName(),
        defaultSubResourceData.getLastName());
  }

  /**
   * convenience function to create resource request object.
   *
   * @return ClaimSubmissionRequest object
   */
  private ClaimSubmissionRequest createResourceRequest() {
    return new ClaimSubmissionRequest(
        defaultResourceData.getUserName(),
        defaultResourceData.getPii(),
        defaultResourceData.getFirstName(),
        defaultResourceData.getLastName(),
        defaultResourceData.getSubmissionId(),
        defaultResourceData.getClaimantId(),
        defaultResourceData.getContentionType());
  }

  /**
   * convenience function to create subresource request object.
   *
   * @return SubClaimSubmissionRequest object
   */
  private SubClaimSubmissionRequest createSubResourceRequest() {
    return new SubClaimSubmissionRequest(
        defaultSubResourceData.getUserName(),
        defaultSubResourceData.getFirstName(),
        defaultSubResourceData.getLastName());
  }

  /**
   * helper function to validate standard values.
   *
   * @param resource the object to validate
   */
  protected void verifyResource(ClaimSubmission resource) {
    assertThat(resource.getUserName().equals(defaultResourceData.getUserName()));
    assertThat(resource.getPii().equals(defaultResourceData.getPii()));
    assertThat(resource.getFirstName().equals(defaultResourceData.getFirstName()));
    assertThat(resource.getLastName().equals(defaultResourceData.getLastName()));
    assertThat(resource.getId()).isNotEqualTo(defaultResourceData.getId());
  }

  /**
   * helper function to validate standard values.
   *
   * @param resource the object to validate
   */
  protected void verifySubResource(SubClaimSubmission resource) {
    assertThat(resource.getUserName().equals(defaultSubResourceData.getUserName()));
    assertThat(resource.getFirstName().equals(defaultSubResourceData.getFirstName()));
    assertThat(resource.getLastName().equals(defaultSubResourceData.getLastName()));
    assertThat(resource.getId()).isNotEqualTo(defaultSubResourceData.getId());
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  private void verifyResourceResponse(ClaimSubmissionResponse response) {
    assertThat(response.getUserName().equals(defaultResourceData.getUserName()));
    assertThat(response.getPii().equals(defaultResourceData.getPii()));
    assertThat(response.getFullName().equals(defaultResourceData.getFullName()));
    assertThat(response.getId()).isEqualTo(defaultResourceData.getId());
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  protected void verifySubResourceResponse(SubClaimSubmissionResponse response) {
    assertThat(response.getId()).isEqualTo(defaultSubResourceData.getId());
  }
}
