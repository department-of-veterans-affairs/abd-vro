package gov.va.starter.example.controller.claimsubmission;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.boot.notifier.lifecycle.entity.provider.MemoizedTimestampProvider;
import gov.va.starter.boot.notifier.lifecycle.entity.provider.NoopEntityLifecycleNotifier;
import gov.va.starter.boot.notifier.lifecycle.entity.spi.EntityLifecycleNotifier;
import gov.va.starter.example.api.claimsubmission.requests.ClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.responses.ClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.claimsubmission.factory.ClaimSubmissionFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import gov.va.starter.example.controller.claimsubmission.mapper.ClaimSubmissionRequestMapper;
import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.starter.example.service.spi.claimsubmission.model.SubClaimSubmission;
import gov.va.starter.example.subclaimsubmission.factory.SubClaimSubmissionFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
import gov.va.vro.service.provider.CamelEntrance;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ClaimSubmissionControllerTest {

  private ClaimSubmissionController controller;

  @Mock private CamelEntrance camelEntrance;
  @Mock private ClaimSubmissionService manager;
  @Mock private ClaimSubmissionRequestMapper mapper;
  private EntityLifecycleNotifier notifier =
      new NoopEntityLifecycleNotifier(new MemoizedTimestampProvider(ZonedDateTime.now()));

  private ClaimSubmission resource;
  private ClaimSubmission output;
  private ClaimSubmissionData defaultResourceData;
  private ClaimSubmissionData bogusResourceData;
  private ClaimSubmissionFactory resourceFactory = new ClaimSubmissionFactory();
  private ClaimSubmissionRequest request;
  private ClaimSubmissionResponse response;
  private Optional<ClaimSubmission> emptyResource = Optional.empty();
  private Optional<ClaimSubmissionResponse> emptyResponse = Optional.empty();
  private Optional<ClaimSubmissionResponse> optionalResponse;
  private Optional<ClaimSubmission> optionalOutput;
  private List<ClaimSubmissionResponse> responseList;
  private List<ClaimSubmission> outputList;
  private List<ClaimSubmissionResponse> emptyResponseList = Arrays.asList();
  private List<ClaimSubmission> emptyOutputList = Arrays.asList();
  private PagedResponse<ClaimSubmissionResponse> responsePage;
  private PagedResponse<ClaimSubmissionResponse> emptyResponsePage;
  private Page<ClaimSubmission> outputPage;
  private Page<ClaimSubmission> emptyOutputPage;
  private Pageable pageable = Pageable.unpaged();

  private SubClaimSubmission subResource;
  private SubClaimSubmission subOutput;
  private SubClaimSubmissionData defaultSubResourceData;
  private SubClaimSubmissionData bogusSubResourceData;
  private SubClaimSubmissionFactory subResourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionRequest subRequest;
  private SubClaimSubmissionResponse subResponse;
  private Optional<SubClaimSubmission> emptySubResource = Optional.empty();
  private Optional<SubClaimSubmissionResponse> emptySubResponse = Optional.empty();
  private Optional<SubClaimSubmissionResponse> optionalSubResponse;
  private Optional<SubClaimSubmission> optionalSubOutput;
  private List<SubClaimSubmissionResponse> subResponseList;
  private List<SubClaimSubmission> subOutputList;
  private List<SubClaimSubmissionResponse> emptySubResponseList = Arrays.asList();
  private List<SubClaimSubmission> emptySubOutputList = Arrays.asList();
  private PagedResponse<SubClaimSubmissionResponse> subResponsePage;
  private PagedResponse<SubClaimSubmissionResponse> emptySubResponsePage;
  private Page<SubClaimSubmission> subOutputPage;
  private Page<SubClaimSubmission> emptySubOutputPage;

  /** setup data for each test. */
  @BeforeEach
  public void setup() {

    controller = new ClaimSubmissionController(camelEntrance, manager, mapper, notifier);

    defaultResourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
    bogusResourceData = resourceFactory.createBySpec("bogus");
    defaultSubResourceData = subResourceFactory.createBySpec(DEFAULT_SPEC);
    bogusSubResourceData = subResourceFactory.createBySpec("bogus");

    // use the real mapper to generate consistent objects to use in mapper stubs
    ClaimSubmissionRequestMapper real = Mappers.getMapper(ClaimSubmissionRequestMapper.class);

    request =
        new ClaimSubmissionRequest(
            defaultResourceData.getUserName(),
            defaultResourceData.getPii(),
            defaultResourceData.getFirstName(),
            defaultResourceData.getLastName(),
            defaultResourceData.getSubmissionId(),
            defaultResourceData.getClaimantId(),
            defaultResourceData.getContentionType());
    resource = real.toModel(request);
    output =
        new ClaimSubmission(
            defaultResourceData.getId(),
            resource.getUserName(),
            resource.getPii(),
            resource.getFirstName(),
            resource.getLastName(),
            resource.getSubmissionId(),
            resource.getClaimantId(),
            resource.getContentionType(),
            resource.getStatus());
    response = real.toClaimSubmissionResponse(output);
    optionalResponse = Optional.of(response);
    optionalOutput = Optional.of(output);
    responseList = Arrays.asList(response, response);
    outputList = Arrays.asList(output, output);
    responsePage = new PagedResponse<>(responseList, 10, (long) 100, 1, 10);
    emptyResponsePage = new PagedResponse<>(emptyResponseList, 0, (long) 0, 0, 0);
    outputPage = new PageImpl<>(outputList);
    emptyOutputPage = new PageImpl<>(emptyOutputList);

    subRequest =
        new SubClaimSubmissionRequest(
            defaultSubResourceData.getUserName(),
            defaultSubResourceData.getFirstName(),
            defaultSubResourceData.getLastName());
    subResource = real.toModel(subRequest);
    subOutput =
        new SubClaimSubmission(
            defaultSubResourceData.getId(),
            subResource.getUserName(),
            subResource.getFirstName(),
            subResource.getLastName());
    subResponse = real.toSubClaimSubmissionResponse(subOutput);
    optionalSubResponse = Optional.of(subResponse);
    optionalSubOutput = Optional.of(subOutput);
    subResponseList = Arrays.asList(subResponse, subResponse);
    subOutputList = Arrays.asList(subOutput, subOutput);
    subResponsePage = new PagedResponse<>(subResponseList, 10, (long) 100, 1, 10);
    emptySubResponsePage = new PagedResponse<>(emptySubResponseList, 0, (long) 0, 0, 0);
    subOutputPage = new PageImpl<>(subOutputList);
    emptySubOutputPage = new PageImpl<>(emptySubOutputList);
  }

  private void createMapperStubs() {
    Mockito.when(mapper.toModel(request)).thenReturn(resource);
  }

  private void createResponseMapperStubs() {
    Mockito.when(mapper.toClaimSubmissionResponse(output)).thenReturn(response);
  }

  private void createOptionalMapperStubs() {
    Mockito.when(mapper.toClaimSubmissionResponse(optionalOutput)).thenReturn(response);
  }

  private void createListMapperStubs() {
    Mockito.when(mapper.toClaimSubmissionResponsePage(outputPage)).thenReturn(responsePage);
  }

  private void createEmptyListMapperStubs() {
    Mockito.when(mapper.toClaimSubmissionResponsePage(emptyOutputPage))
        .thenReturn(emptyResponsePage);
  }

  private void createSubClaimSubmissionMapperStubs() {
    Mockito.when(mapper.toModel(subRequest)).thenReturn(subResource);
  }

  private void createSubClaimSubmissionResponseMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponse(subOutput)).thenReturn(subResponse);
  }

  private void createOptionalSubClaimSubmissionMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponse(optionalSubOutput)).thenReturn(subResponse);
  }

  private void createSubClaimSubmissionListMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponsePage(subOutputPage))
        .thenReturn(subResponsePage);
  }

  private void createEmptySubClaimSubmissionListMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponsePage(emptySubOutputPage))
        .thenReturn(emptySubResponsePage);
  }

  @Test
  public void findByResourceIdFailTest() throws Exception {

    Mockito.when(manager.findById(bogusResourceData.getId())).thenReturn(emptyResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<ClaimSubmissionResponse> response =
              controller.findEntityById(bogusResourceData.getId());
        });
  }

  @Test
  public void addResourceTest() throws Exception {

    createMapperStubs();
    createResponseMapperStubs();
    Mockito.when(manager.add(resource)).thenReturn(output);

    ResponseEntity<ClaimSubmissionResponse> response = controller.addEntity(request);

    assertThat(response.getStatusCodeValue()).isEqualTo(201);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getPii()).isEqualTo(defaultResourceData.getPii());
    assertThat(response.getBody().getFullName()).isEqualTo(defaultResourceData.getFullName());
    assertThat(response.getBody().getId()).isEqualTo(defaultResourceData.getId());
  }

  @Test
  public void findByIdTest() throws Exception {

    createResponseMapperStubs();
    Mockito.when(manager.findById(defaultResourceData.getId())).thenReturn(optionalOutput);

    ResponseEntity<ClaimSubmissionResponse> response =
        controller.findEntityById(defaultResourceData.getId());

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getPii()).isEqualTo(defaultResourceData.getPii());
    assertThat(response.getBody().getFullName()).isEqualTo(defaultResourceData.getFullName());
    assertThat(response.getBody().getId()).isEqualTo(defaultResourceData.getId());
  }

  @Test
  public void findByIdFailedTest() throws Exception {

    Mockito.when(manager.findById(bogusResourceData.getId())).thenReturn(emptyResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<ClaimSubmissionResponse> response =
              controller.findEntityById(bogusResourceData.getId());
        });
  }

  @Test
  public void findAllTest() throws Exception {

    createListMapperStubs();
    Mockito.when(manager.findAll(pageable)).thenReturn(outputPage);

    ResponseEntity<PagedResponse<ClaimSubmissionResponse>> response =
        controller.findEntities(pageable);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getItems().size()).isEqualTo(2);
    // Todo: check contents of the list objects
  }

  @Test
  public void findAllEmptyTest() throws Exception {

    createEmptyListMapperStubs();
    Mockito.when(manager.findAll(pageable)).thenReturn(emptyOutputPage);

    ResponseEntity<PagedResponse<ClaimSubmissionResponse>> response =
        controller.findEntities(pageable);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getItems().size()).isEqualTo(0);
  }

  @Test
  public void updateTest() throws Exception {

    createMapperStubs();
    createResponseMapperStubs();
    Mockito.when(manager.updateById(defaultResourceData.getId(), resource))
        .thenReturn(optionalOutput);

    ResponseEntity<ClaimSubmissionResponse> response =
        controller.updateEntityById(defaultResourceData.getId(), request);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getPii()).isEqualTo(defaultResourceData.getPii());
    assertThat(response.getBody().getFullName()).isEqualTo(defaultResourceData.getFullName());
    assertThat(response.getBody().getId()).isEqualTo(defaultResourceData.getId());
  }

  @Test
  public void updateFailedTest() throws Exception {

    createMapperStubs();
    Mockito.when(manager.updateById(bogusResourceData.getId(), resource)).thenReturn(emptyResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<ClaimSubmissionResponse> response =
              controller.updateEntityById(bogusResourceData.getId(), request);
        });
  }

  @Test
  public void deleteTest() throws Exception {

    createResponseMapperStubs();
    Mockito.when(manager.deleteById(defaultResourceData.getId())).thenReturn(optionalOutput);

    ResponseEntity<ClaimSubmissionResponse> response =
        controller.deleteEntityById(defaultResourceData.getId());

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getPii()).isEqualTo(defaultResourceData.getPii());
    assertThat(response.getBody().getFullName()).isEqualTo(defaultResourceData.getFullName());
    assertThat(response.getBody().getId()).isEqualTo(defaultResourceData.getId());
  }

  @Test
  public void deleteFailedTest() throws Exception {

    Mockito.when(manager.deleteById(bogusResourceData.getId())).thenReturn(emptyResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<ClaimSubmissionResponse> response =
              controller.deleteEntityById(bogusResourceData.getId());
        });
  }

  @Test
  public void findBySubResourceIdFailTest() throws Exception {

    Mockito.when(
            manager.getSubClaimSubmission(bogusResourceData.getId(), bogusSubResourceData.getId()))
        .thenReturn(emptySubResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.getSubClaimSubmission(
                  bogusResourceData.getId(), bogusSubResourceData.getId());
        });
  }

  @Test
  public void addSubResourceTest() throws Exception {

    createSubClaimSubmissionMapperStubs();
    createSubClaimSubmissionResponseMapperStubs();
    Mockito.when(manager.addSubClaimSubmission(defaultResourceData.getId(), subResource))
        .thenReturn(subOutput);

    ResponseEntity<SubClaimSubmissionResponse> response =
        controller.addSubClaimSubmission(defaultResourceData.getId(), subRequest);

    assertThat(response.getStatusCodeValue()).isEqualTo(201);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(defaultSubResourceData.getId());
  }

  @Test
  public void findSubResourceByIdTest() throws Exception {

    createSubClaimSubmissionResponseMapperStubs();
    Mockito.when(
            manager.getSubClaimSubmission(
                defaultResourceData.getId(), defaultSubResourceData.getId()))
        .thenReturn(optionalSubOutput);

    ResponseEntity<SubClaimSubmissionResponse> response =
        controller.getSubClaimSubmission(
            defaultResourceData.getId(), defaultSubResourceData.getId());

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getId()).isEqualTo(defaultSubResourceData.getId());
  }

  @Test
  public void findSubResourceByIdFailedTest() throws Exception {

    Mockito.when(
            manager.getSubClaimSubmission(bogusResourceData.getId(), bogusSubResourceData.getId()))
        .thenReturn(emptySubResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.getSubClaimSubmission(
                  bogusResourceData.getId(), bogusSubResourceData.getId());
        });
  }

  @Test
  public void findAllSubResourceTest() throws Exception {

    createSubClaimSubmissionListMapperStubs();
    Mockito.when(manager.getSubClaimSubmissions(defaultResourceData.getId(), pageable))
        .thenReturn(subOutputPage);

    ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> response =
        controller.getSubClaimSubmissions(defaultResourceData.getId(), pageable);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getItems().size()).isEqualTo(2);
    // Todo: check contents of the list objects
  }

  @Test
  public void findAllSubResourceEmptyTest() throws Exception {

    createEmptySubClaimSubmissionListMapperStubs();
    Mockito.when(manager.getSubClaimSubmissions(defaultResourceData.getId(), pageable))
        .thenReturn(emptySubOutputPage);

    ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> response =
        controller.getSubClaimSubmissions(defaultResourceData.getId(), pageable);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getItems().size()).isEqualTo(0);
  }

  @Test
  public void updateSubResourceTest() throws Exception {

    createSubClaimSubmissionMapperStubs();
    createSubClaimSubmissionResponseMapperStubs();
    Mockito.when(
            manager.updateSubClaimSubmission(
                defaultResourceData.getId(), defaultSubResourceData.getId(), subResource))
        .thenReturn(optionalSubOutput);

    ResponseEntity<SubClaimSubmissionResponse> response =
        controller.updateSubClaimSubmission(
            defaultResourceData.getId(), defaultSubResourceData.getId(), subRequest);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getId()).isEqualTo(defaultSubResourceData.getId());
  }

  @Test
  public void updateSubResourceFailedTest() throws Exception {

    createSubClaimSubmissionMapperStubs();
    Mockito.when(
            manager.updateSubClaimSubmission(
                bogusResourceData.getId(), bogusSubResourceData.getId(), subResource))
        .thenReturn(emptySubResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.updateSubClaimSubmission(
                  bogusResourceData.getId(), bogusSubResourceData.getId(), subRequest);
        });
  }

  @Test
  public void deleteSubResourceTest() throws Exception {

    createSubClaimSubmissionResponseMapperStubs();
    Mockito.when(
            manager.deleteSubClaimSubmission(
                defaultResourceData.getId(), defaultSubResourceData.getId()))
        .thenReturn(optionalSubOutput);

    ResponseEntity<SubClaimSubmissionResponse> response =
        controller.deleteSubClaimSubmission(
            defaultResourceData.getId(), defaultSubResourceData.getId());

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getId()).isEqualTo(defaultSubResourceData.getId());
  }

  @Test
  public void deleteSubResourceFailedTest() throws Exception {

    Mockito.when(
            manager.deleteSubClaimSubmission(
                bogusResourceData.getId(), bogusSubResourceData.getId()))
        .thenReturn(emptySubResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.deleteSubClaimSubmission(
                  bogusResourceData.getId(), bogusSubResourceData.getId());
        });
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
