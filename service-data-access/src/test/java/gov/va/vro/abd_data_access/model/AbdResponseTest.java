package gov.va.vro.abd_data_access.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author warren
 * @Date 9/6/22
 */
class AbdResponseTest {

    private static final String TEST_ICN = "90203902";
    private static final String TEST_DIAG_CODE = "9020";
    private static final String TEST_CLAIM_ID = "2000";
    private static final String TEST_TEXT = "some text";
    private static final String TEST_CODE = "8000";
    private static final String TEST_STATUS = "ACTIVE";
    private static final String TEST_DATE = "09/05/2022";
    private static final String TEST_CODESYSTEM = "codesystem";
    private static final String TEST_DURATION = "one month duration";
    private static final String TEST_ROUTE = "route";
    private static final String TEST_INSTRUCTION = "instruction";
    private static final int TEST_REFILLS = 5;

    @Test
    public void test() {
        AbdClaim claim = new AbdClaim(TEST_ICN, TEST_DIAG_CODE, TEST_CLAIM_ID);

        AbdEvidence evidence = new AbdEvidence();

        AbdProcedure procedure = new AbdProcedure();
        procedure.setCode(TEST_CODE);
        procedure.setCodeSystem(TEST_CODESYSTEM);
        procedure.setPerformedDate(TEST_DATE);
        procedure.setStatus(TEST_STATUS);
        procedure.setText(TEST_TEXT);

        AbdCondition condition = new AbdCondition();
        condition.setCode(TEST_CODE);
        condition.setStatus(TEST_STATUS);
        condition.setAbatementDate(TEST_DATE);
        condition.setText(TEST_TEXT);
        condition.setOnsetDate(TEST_DATE);

        AbdMedication medication = new AbdMedication();
        medication.setDescription(TEST_TEXT);
        medication.setAuthoredOn(TEST_DATE);
        medication.setDuration(TEST_DURATION);
        medication.setNotes(Collections.singletonList(TEST_TEXT));
        medication.setRefills(TEST_REFILLS);
        medication.setRoute(TEST_ROUTE);
        medication.setDosageInstructions(Collections.singletonList(TEST_INSTRUCTION));

        AbdBloodPressure bloodPressure = new AbdBloodPressure();

        evidence.setConditions(Collections.singletonList(condition));
        evidence.setProcedures(Collections.singletonList(procedure));
        evidence.setMedications(Collections.singletonList(medication));
        evidence.setBloodPressures(Collections.singletonList(bloodPressure));

        AbdResponse resp1 = new AbdResponse();
        AbdResponse resp2 = new AbdResponse(claim);
        AbdResponse resp3 = new AbdResponse(claim, evidence);
        AbdResponse resp4 = new AbdResponse(claim, TEST_TEXT);

        assertNull(resp1.getDiagnosticCode());
        assertNull(resp1.getVeteranIcn());
        assertNull(resp1.getErrorMessage());
        assertNull(resp1.getEvidence());

        assertEquals(TEST_ICN, resp2.getVeteranIcn());
        assertEquals(TEST_DIAG_CODE, resp2.getDiagnosticCode());
        assertNull(resp2.getErrorMessage());
        assertNull(resp2.getEvidence());

        assertEquals(resp2.getDiagnosticCode(), resp3.getDiagnosticCode());
        assertEquals(resp2.getVeteranIcn(), resp3.getVeteranIcn());
        assertNotNull(resp3.getEvidence());
        assertEquals(medication, resp3.getEvidence().getMedications().get(0));
        assertEquals(bloodPressure, resp3.getEvidence().getBloodPressures().get(0));
        assertEquals(procedure, resp3.getEvidence().getProcedures().get(0));
        assertEquals(condition, resp3.getEvidence().getConditions().get(0));

        assertEquals(TEST_TEXT, resp4.getErrorMessage());
    }
}