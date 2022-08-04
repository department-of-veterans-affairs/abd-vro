package gov.va.vro;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.provider.camel.SlipClaimSubmitRouter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VroControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @MockBean
    private SlipClaimSubmitRouter slipClaimSubmitRouter;

    @Autowired
    @InjectMocks
    private PrimaryRoutes primaryRoutes;



    @Test
    void postHealthAssessment() {

        Mockito.when(slipClaimSubmitRouter.routeClaimSubmit(Mockito.any(), Mockito.anyMap()))
                .thenReturn("direct:hello");

        HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
        request.setClaimSubmissionId("1234");
        request.setVeteranIcn("icn");
        request.setDiagnosticCode("1701");

        ResponseEntity<HealthDataAssessmentResponse> responseEntity =
                testRestTemplate.postForEntity("/v1/health-data-assessment", request, HealthDataAssessmentResponse.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        HealthDataAssessmentResponse response = responseEntity.getBody();
        assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
        assertEquals(request.getVeteranIcn(), response.getVeteranIcn());
    }
}
