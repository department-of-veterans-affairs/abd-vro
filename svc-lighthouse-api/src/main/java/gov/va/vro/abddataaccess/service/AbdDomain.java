package gov.va.vro.abddataaccess.service;

/**
 * Represents medical data domains used in decision rules in Automated Benefit Delivery health
 * assessment.
 */
public enum AbdDomain {
  MEDICATION("launch patient/MedicationRequest.read"),
  BLOOD_PRESSURE("launch patient/Observation.read"),
  PROCEDURE("launch patient/Procedure.read"),
  CONDITION("launch patient/Condition.read"),
  ENCOUNTER("launch patient/Encounter.read");

  private final String scope;

  AbdDomain(String scope) {
    this.scope = scope;
  }

  public String getScope() {
    return scope;
  }
}
