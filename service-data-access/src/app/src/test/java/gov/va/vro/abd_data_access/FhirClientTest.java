package gov.va.vro.abd_data_access;

import gov.va.vro.abd_data_access.model.AbdEvidence;
import gov.va.vro.abd_data_access.service.FhirClient;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles("test")
class FhirClientTest {
    @Autowired
    private FhirClient client;

    @Value("classpath:expected-json/lh-patient01-7101.json")
    private Resource expectedResource;

    @Test
    public void testLighthouseAPIAccess() throws Exception {
        AbdEvidence evidence = client.getMedicalEvidence(CommonData.claim01);

        assertNotNull(evidence);
        assertNotNull(evidence.getBloodPressures());
        assertTrue(evidence.getBloodPressures().size() > 0);
            
        ObjectMapper mapper = new ObjectMapper();
        String actual = mapper.writeValueAsString(evidence);

        InputStream stream = expectedResource.getInputStream();
        String expected = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}