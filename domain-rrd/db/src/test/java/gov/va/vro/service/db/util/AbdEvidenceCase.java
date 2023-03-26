package gov.va.vro.service.db.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.va.vro.model.AbdBloodPressure;
import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.AbdProcedure;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** This class contains methods to generate AbdEvidence objects for testing purposes. */
@Getter
public class AbdEvidenceCase {
  private static final AtomicInteger counter = new AtomicInteger(1);

  private static final int MED_OFFSET = 3;
  private static final int CONDITION_OFFSET = 5;
  private static final int PROCEDURE_OFFSET = 7;
  private static final int BP_OFFSET = 1;

  private static final int MOD_LENGTH = 11;

  private Integer medCount;
  private Integer conditionCount;
  private Integer procedureCount;
  private Integer bpCount;

  private static Integer getCount(int index, int offset) {
    int count = index + offset;
    if (count == MOD_LENGTH) {
      return null;
    }
    return (count - 1) % MOD_LENGTH;
  }

  private static <T> List<T> getList(Integer count, Class<T> clazz) {
    if (count == null) {
      return null;
    }
    return Collections.nCopies(count.intValue(), (T) null);
  }

  private AbdEvidence generateEvidence() {
    AbdEvidence evidence = new AbdEvidence();

    evidence.setBloodPressures(getList(bpCount, AbdBloodPressure.class));
    evidence.setMedications(getList(medCount, AbdMedication.class));
    evidence.setConditions(getList(conditionCount, AbdCondition.class));
    evidence.setProcedures(getList(procedureCount, AbdProcedure.class));

    return evidence;
  }

  private Map<String, Object> generateSummary() {
    Map<String, Object> result = new HashMap<>();

    if (bpCount != null) {
      result.put("totalBpReadings", bpCount);
    }
    if (procedureCount != null) {
      result.put("proceduresCount", procedureCount);
    }
    if (conditionCount != null) {
      result.put("conditionsCount", conditionCount);
    }
    if (medCount != null) {
      result.put("medicationsCount", medCount);
    }
    return result;
  }

  /**
   * Generates the evidence summary for the test case.
   *
   * @param claimSubmissionId claim id for the evidence summary
   * @return AbdEvidenceWithSummary
   */
  public AbdEvidenceWithSummary getEvidenceWithSummary(String claimSubmissionId) {
    AbdEvidenceWithSummary result = new AbdEvidenceWithSummary();

    result.setClaimSubmissionId(claimSubmissionId);
    result.setEvidence(generateEvidence());
    result.setEvidenceSummary(generateSummary());

    return result;
  }

  private static void verifyEvidenceField(Integer expected, String actual) {
    if (expected == null) {
      assertNull(actual);
    } else {
      assertEquals(expected.toString(), actual);
    }
  }

  /**
   * Verifies the test case has the expected evidence summary.
   *
   * @param evidenceSummary Actual evidence summary to be verified.
   */
  public void verifyEvidenceSummary(Map<String, String> evidenceSummary) {
    verifyEvidenceField(bpCount, evidenceSummary.get("totalBpReadings"));
    verifyEvidenceField(procedureCount, evidenceSummary.get("proceduresCount"));
    verifyEvidenceField(medCount, evidenceSummary.get("medicationsCount"));
  }

  /**
   * Generates an evidence summary test case.
   *
   * @return AbdEvidenceCase
   */
  public static AbdEvidenceCase getInstance() {
    int index = counter.incrementAndGet();

    AbdEvidenceCase result = new AbdEvidenceCase();
    result.medCount = getCount(index, MED_OFFSET);
    result.conditionCount = getCount(index, CONDITION_OFFSET);
    result.procedureCount = getCount(index, PROCEDURE_OFFSET);
    result.bpCount = getCount(index, BP_OFFSET);

    return result;
  }
}
