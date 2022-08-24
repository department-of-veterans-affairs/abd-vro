package gov.va.vro.abd_data_access.service;

import gov.va.vro.abd_data_access.model.AbdCondition;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


class FieldExtractorTest {

    private final static String TEST_CODE = "test";

    @Test
    public void testExtractCondition() {
        System.out.println("test ExtractCondition");
        Condition testCondition = new Condition();
        CodeableConcept code = new CodeableConcept();
        code.setText(TEST_CODE);
        testCondition.setCode(code);
        AbdCondition abdCondition = FieldExtractor.extractCondition(testCondition);
        assertEquals(TEST_CODE, abdCondition.getText());
    }
}