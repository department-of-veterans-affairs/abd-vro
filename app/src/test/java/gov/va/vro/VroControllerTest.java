package gov.va.vro;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class VroControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void postHealthAssessment() {
        HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
        request.setClaimSubmissionId("1234");
        request.setVeteranIcn("icn");
        request.setDiagnosticCode("1701");
        //TODO
//        ResponseEntity<HealthDataAssessmentResponse> responseEntity =
//                testRestTemplate.postForEntity("/v1/health-data-assessment", request, HealthDataAssessmentResponse.class);
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        HealthDataAssessmentResponse response = responseEntity.getBody();
//        assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
//        assertEquals(request.getVeteranIcn(), response.getVeteranIcn());
    }
}
