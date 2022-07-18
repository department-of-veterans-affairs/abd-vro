package gov.va.starter.example.controller.subclaimsubmission;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.boot.notifier.lifecycle.entity.provider.MemoizedTimestampProvider;
import gov.va.starter.boot.notifier.lifecycle.entity.provider.NoopEntityLifecycleNotifier;
import gov.va.starter.boot.notifier.lifecycle.entity.spi.EntityLifecycleNotifier;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.api.subclaimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.subclaimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.controller.subclaimsubmission.mapper.SubClaimSubmissionRequestMapper;
import gov.va.starter.example.service.spi.subclaimsubmission.SubClaimSubmissionService;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
import gov.va.starter.example.subclaimsubmission.factory.SubClaimSubmissionFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
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
public class SubClaimSubmissionControllerTest {

  private SubClaimSubmissionController controller;

  @Mock private SubClaimSubmissionService manager;
  @Mock private SubClaimSubmissionRequestMapper mapper;
  private EntityLifecycleNotifier notifier =
      new NoopEntityLifecycleNotifier(new MemoizedTimestampProvider(ZonedDateTime.now()));

  private SubClaimSubmission resource;
  private SubClaimSubmission output;
  private SubClaimSubmissionData defaultResourceData;
  private SubClaimSubmissionData bogusResourceData;
  private SubClaimSubmissionFactory resourceFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionRequest request;
  private SubClaimSubmissionResponse response;
  private Optional<SubClaimSubmission> emptyResource = Optional.empty();
  private Optional<SubClaimSubmissionResponse> emptyResponse = Optional.empty();
  private Optional<SubClaimSubmissionResponse> optionalResponse;
  private Optional<SubClaimSubmission> optionalOutput;
  private List<SubClaimSubmissionResponse> responseList;
  private List<SubClaimSubmission> outputList;
  private List<SubClaimSubmissionResponse> emptyResponseList = Arrays.asList();
  private List<SubClaimSubmission> emptyOutputList = Arrays.asList();
  private PagedResponse<SubClaimSubmissionResponse> responsePage;
  private PagedResponse<SubClaimSubmissionResponse> emptyResponsePage;
  private Page<SubClaimSubmission> outputPage;
  private Page<SubClaimSubmission> emptyOutputPage;
  private Pageable pageable = Pageable.unpaged();

  /** setup data for each test. */
  @BeforeEach
  public void setup() {

    controller = new SubClaimSubmissionController(manager, mapper, notifier);

    defaultResourceData = resourceFactory.createBySpec(DEFAULT_SPEC);
    bogusResourceData = resourceFactory.createBySpec("bogus");

    // use the real mapper to generate consistent objects to use in mapper stubs
    SubClaimSubmissionRequestMapper real = Mappers.getMapper(SubClaimSubmissionRequestMapper.class);

    request =
        new SubClaimSubmissionRequest(
            defaultResourceData.getUserName(),
            defaultResourceData.getPii(),
            defaultResourceData.getFirstName(),
            defaultResourceData.getLastName());
    resource = real.toModel(request);
    output =
        new SubClaimSubmission(
            defaultResourceData.getId(),
            resource.getUserName(),
            resource.getPii(),
            resource.getFirstName(),
            resource.getLastName());
    response = real.toSubClaimSubmissionResponse(output);
    optionalResponse = Optional.of(response);
    optionalOutput = Optional.of(output);
    responseList = Arrays.asList(response, response);
    outputList = Arrays.asList(output, output);
    responsePage = new PagedResponse<>(responseList, 10, (long) 100, 1, 10);
    emptyResponsePage = new PagedResponse<>(emptyResponseList, 0, (long) 0, 0, 0);
    outputPage = new PageImpl<>(outputList);
    emptyOutputPage = new PageImpl<>(emptyOutputList);
  }

  private void createMapperStubs() {
    Mockito.when(mapper.toModel(request)).thenReturn(resource);
  }

  private void createResponseMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponse(output)).thenReturn(response);
  }

  private void createOptionalMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponse(optionalOutput)).thenReturn(response);
  }

  private void createListMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponsePage(outputPage)).thenReturn(responsePage);
  }

  private void createEmptyListMapperStubs() {
    Mockito.when(mapper.toSubClaimSubmissionResponsePage(emptyOutputPage))
        .thenReturn(emptyResponsePage);
  }

  @Test
  public void findByResourceIdFailTest() throws Exception {

    Mockito.when(manager.findById(bogusResourceData.getId())).thenReturn(emptyResource);

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.findEntityById(bogusResourceData.getId());
        });
  }

  @Test
  public void addResourceTest() throws Exception {

    createMapperStubs();
    createResponseMapperStubs();
    Mockito.when(manager.add(resource)).thenReturn(output);

    ResponseEntity<SubClaimSubmissionResponse> response = controller.addEntity(request);

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

    ResponseEntity<SubClaimSubmissionResponse> response =
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
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.findEntityById(bogusResourceData.getId());
        });
  }

  @Test
  public void findAllTest() throws Exception {

    createListMapperStubs();
    Mockito.when(manager.findAll(pageable)).thenReturn(outputPage);

    ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> response =
        controller.findEntities(pageable);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody().getItems().size()).isEqualTo(2);
    // Todo: check contents of the list objects
  }

  @Test
  public void findAllEmptyTest() throws Exception {

    createEmptyListMapperStubs();
    Mockito.when(manager.findAll(pageable)).thenReturn(emptyOutputPage);

    ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> response =
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

    ResponseEntity<SubClaimSubmissionResponse> response =
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
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.updateEntityById(bogusResourceData.getId(), request);
        });
  }

  @Test
  public void deleteTest() throws Exception {

    createResponseMapperStubs();
    Mockito.when(manager.deleteById(defaultResourceData.getId())).thenReturn(optionalOutput);

    ResponseEntity<SubClaimSubmissionResponse> response =
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
          ResponseEntity<SubClaimSubmissionResponse> response =
              controller.deleteEntityById(bogusResourceData.getId());
        });
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
