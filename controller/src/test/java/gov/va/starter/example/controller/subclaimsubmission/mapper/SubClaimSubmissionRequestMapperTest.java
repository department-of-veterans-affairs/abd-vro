package gov.va.starter.example.controller.subclaimsubmission.mapper;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.api.subclaimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.subclaimsubmission.responses.SubClaimSubmissionResponse;
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

public class SubClaimSubmissionRequestMapperTest {

  private SubClaimSubmissionRequestMapper mapper;
  private SubClaimSubmissionFactory resourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionData defaultResourceData = resourceFactory.createBySpec(DEFAULT_SPEC);

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(SubClaimSubmissionRequestMapper.class);
  }

  @Test
  public void mapperNewResourceTest() {
    SubClaimSubmissionRequest resource = createResourceRequest();

    SubClaimSubmission response = mapper.toModel(resource);

    verifyResource(response);
  }

  @Test
  public void mapperResourceResponseTest() {
    SubClaimSubmission resource = createResource(defaultResourceData.getId());

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    verifyResourceResponse(response);
  }

  @Test
  public void mapperOptionalTest() {
    Optional<SubClaimSubmission> resource =
        Optional.of(createResource(defaultResourceData.getId()));

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    assertThat(response).isNotNull();
    verifyResourceResponse(response);
  }

  @Test
  public void mapperOptionalNullTest() {
    Optional<SubClaimSubmission> resource = Optional.ofNullable(null);

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    assertThat(response).isNull();
  }

  @Test
  public void mapperOptionalEmptyTest() {
    Optional<SubClaimSubmission> resource = Optional.empty();

    SubClaimSubmissionResponse response = mapper.toSubClaimSubmissionResponse(resource);

    assertThat(response).isNull();
  }

  @Test
  public void mapperEntityListTest() {
    List<SubClaimSubmission> resources =
        Arrays.asList(
            createResource(defaultResourceData.getId()),
            createResource(defaultResourceData.getId()));

    List<SubClaimSubmissionResponse> response = mapper.toSubClaimSubmissionResponseList(resources);

    assertThat(response.size()).isEqualTo(2);
    verifyResourceResponse(response.get(0));
    verifyResourceResponse(response.get(1));
  }

  @Test
  public void mapperEntityPageTest() {
    Pageable pageable = PageRequest.of(0, 1);
    Page<SubClaimSubmission> resources =
        new PageImpl<>(Arrays.asList(createResource(defaultResourceData.getId())), pageable, 100);
    PagedResponse<SubClaimSubmissionResponse> response =
        mapper.toSubClaimSubmissionResponsePage(resources);

    assertThat(response.getItems().size()).isEqualTo(1);
    assertThat(response.getTotalItems()).isEqualTo(100);
    assertThat(response.getPageNumber()).isEqualTo(0);
    assertThat(response.getPageSize()).isEqualTo(1);
    assertThat(response.getTotalPages()).isEqualTo(100);
    verifyResourceResponse(response.getItems().get(0));
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
        defaultResourceData.getUserName(),
        defaultResourceData.getPii(),
        defaultResourceData.getFirstName(),
        defaultResourceData.getLastName());
  }

  /**
   * convenience function to create resource request object.
   *
   * @return SubClaimSubmissionRequest object
   */
  private SubClaimSubmissionRequest createResourceRequest() {
    return new SubClaimSubmissionRequest(
        defaultResourceData.getUserName(),
        defaultResourceData.getPii(),
        defaultResourceData.getFirstName(),
        defaultResourceData.getLastName());
  }

  /**
   * helper function to validate standard values.
   *
   * @param resource the object to validate
   */
  protected void verifyResource(SubClaimSubmission resource) {
    assertThat(resource.getUserName().equals(defaultResourceData.getUserName()));
    assertThat(resource.getPii().equals(defaultResourceData.getPii()));
    assertThat(resource.getFirstName().equals(defaultResourceData.getFirstName()));
    assertThat(resource.getLastName().equals(defaultResourceData.getLastName()));
    assertThat(resource.getId()).isNotEqualTo(defaultResourceData.getId());
  }

  /**
   * helper function to validate standard values.
   *
   * @param response the object to validate
   */
  private void verifyResourceResponse(SubClaimSubmissionResponse response) {
    assertThat(response.getUserName().equals(defaultResourceData.getUserName()));
    assertThat(response.getPii().equals(defaultResourceData.getPii()));
    assertThat(response.getFullName().equals(defaultResourceData.getFullName()));
    assertThat(response.getId()).isEqualTo(defaultResourceData.getId());
  }
}
