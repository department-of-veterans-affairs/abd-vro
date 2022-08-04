package gov.va.starter.example.controller.subclaimsubmission;

import static gov.va.starter.boot.test.data.provider.NamedDataFactory.DEFAULT_SPEC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.errorhandling.advice.ErrorHandlerAdvice;
import gov.va.starter.boot.errorhandling.config.ErrorHandlerConfig;
import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.boot.openapi.config.OpenApiConfiguration;
import gov.va.starter.example.SecurityAllowConfig;
import gov.va.starter.example.api.subclaimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.subclaimsubmission.resources.SubClaimSubmissionResource;
import gov.va.starter.example.controller.subclaimsubmission.mapper.SubClaimSubmissionRequestMapper;
import gov.va.starter.example.service.spi.subclaimsubmission.SubClaimSubmissionService;
import gov.va.starter.example.service.spi.subclaimsubmission.model.SubClaimSubmission;
import gov.va.starter.example.subclaimsubmission.factory.SubClaimSubmissionFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.Problem;

@Deprecated
@Slf4j
@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(SubClaimSubmissionResource.class)
@ContextConfiguration(
    classes = {
      SecurityAllowConfig.class,
      ErrorHandlerAdvice.class,
      OpenApiConfiguration.class,
      ErrorHandlerConfig.class
    })
class SubClaimSubmissionErrorHandlingContextTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private SubClaimSubmissionRequestMapper mapper;

  @MockBean private SubClaimSubmissionService service;

  @MockBean private SubClaimSubmissionResource controller;

  private final String message = "message";
  private final String traceHeaderName = "X-B3-TraceId";
  private final String traceInfo = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";
  private final String baseUrl = "https://starter.va.gov";
  private final String notFoundType = String.format("%s/not-found", baseUrl);
  private final String requestValidationType = String.format("%s/request-validation", baseUrl);

  private SubClaimSubmissionFactory subClaimSubmissionFactory = new SubClaimSubmissionFactory();
  private SubClaimSubmissionData defaultSubClaimSubmission =
      subClaimSubmissionFactory.createBySpec(DEFAULT_SPEC);

  // This object will be magically initialized by the initFields method below.

  @Autowired private JacksonTester<SubClaimSubmissionRequest> jsonRequest;
  private SubClaimSubmissionRequest request =
      new SubClaimSubmissionRequest(
          defaultSubClaimSubmission.getUserName(),
          defaultSubClaimSubmission.getPii(),
          defaultSubClaimSubmission.getFirstName(),
          defaultSubClaimSubmission.getLastName());
  private SubClaimSubmission model =
      new SubClaimSubmission(
          defaultSubClaimSubmission.getUserName(),
          defaultSubClaimSubmission.getPii(),
          defaultSubClaimSubmission.getFirstName(),
          defaultSubClaimSubmission.getLastName());

  @Test
  void whenResourceNotRetrieved_thenReturns404() throws Exception {
    Mockito.when(controller.findEntityById("foo")).thenThrow(new ResourceNotFoundException("foo"));

    // when
    MockHttpServletResponse response =
        mockMvc
            .perform(
                get("/v1/example/subclaimsubmissions/foo")
                    .header(traceHeaderName, traceInfo)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    String content = response.getContentAsString();
    log.info("result [{}]", content);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

    Problem error = objectMapper.readValue(content, Problem.class);
    assertThat(error.getStatus().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(error.getType().toString()).isEqualTo(notFoundType);
    assertThat(error.getInstance().toString())
        .isEqualTo(String.format("%s/%s", baseUrl, traceInfo));
    assertThat(error.getDetail()).isEqualTo("Resource 'foo' not found");
  }

  @Test
  void whenResourceNotFound_thenReturns404() throws Exception {
    Mockito.when(controller.findEntityById("foo")).thenThrow(new ResourceNotFoundException("foo"));

    // when
    MockHttpServletResponse response =
        mockMvc
            .perform(
                get("/v1/example/subclaimsubmissions/foo")
                    .header(traceHeaderName, traceInfo)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    String content = response.getContentAsString();
    log.info("result [{}]", content);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

    Problem error = objectMapper.readValue(content, Problem.class);
    assertThat(error.getStatus().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(error.getType().toString()).isEqualTo(notFoundType);
    assertThat(error.getInstance().toString())
        .isEqualTo(String.format("%s/%s", baseUrl, traceInfo));
    assertThat(error.getDetail()).isEqualTo("Resource 'foo' not found");
  }

  @Test
  void whenHttpMessageNotReadable_thenReturns400() throws Exception {

    String requestMessage =
        "{ \"userName\": null, \"pii\": null, \"firstName\": null, \"lastName\": null}";

    // when
    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/v1/example/subclaimsubmissions")
                    .header(traceHeaderName, traceInfo)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestMessage))
            .andReturn()
            .getResponse();

    String content = response.getContentAsString();
    log.info("result [{}]", content);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    Problem error = objectMapper.readValue(content, Problem.class);
    assertThat(error.getStatus().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(error.getType().toString()).isEqualTo(requestValidationType);
    assertThat(error.getInstance().toString())
        .isEqualTo(String.format("%s/%s", baseUrl, traceInfo));
    assertThat(error.getDetail()).contains("userName is marked non-null but is null");
  }

  @Test
  void whenRequestNotValid_thenReturns400() throws Exception {
    Mockito.when(controller.addEntity(Mockito.any()))
        .thenThrow(new RequestValidationException(message));

    // when
    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/v1/example/subclaimsubmissions")
                    .header(traceHeaderName, traceInfo)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(request).getJson()))
            .andReturn()
            .getResponse();

    String content = response.getContentAsString();
    log.info("result [{}]", content);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    Problem error = objectMapper.readValue(content, Problem.class);
    assertThat(error.getStatus().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(error.getType().toString()).isEqualTo(requestValidationType);
    assertThat(error.getInstance().toString())
        .isEqualTo(String.format("%s/%s", baseUrl, traceInfo));
    assertThat(error.getDetail()).isEqualTo("Resource 'message' invalid request");
  }

  @Test
  void whenRequestBodyMissing_thenReturns400() throws Exception {

    // when
    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/v1/example/subclaimsubmissions")
                    .header(traceHeaderName, traceInfo)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    String content = response.getContentAsString();
    log.info("result [{}]", content);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    Problem error = objectMapper.readValue(content, Problem.class);
    assertThat(error.getStatus().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(error.getType().toString()).isEqualTo(requestValidationType);
    assertThat(error.getInstance().toString())
        .isEqualTo(String.format("%s/%s", baseUrl, traceInfo));
    assertThat(error.getDetail()).contains("Required request body is missing");
  }
}
