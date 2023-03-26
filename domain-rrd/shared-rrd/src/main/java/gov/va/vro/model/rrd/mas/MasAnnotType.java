package gov.va.vro.model.rrd.mas;

/**
 * Represents medical data domains used in decision rules in Automated Benefit Delivery health
 * assessment.
 */
public enum MasAnnotType {
  MEDICATION("medication"),
  LABRESULT("lab_result"),

  BLOOD_PRESSURE("blood_pressure"),

  PROCEDURE("procedure"),
  SERVICE("service"),
  CONDITION("medical_condition"),
  UNKNOWN("");

  private final String masAnnotTypeText;

  MasAnnotType(String masAnnotTypeText) {
    this.masAnnotTypeText = masAnnotTypeText;
  }

  public String getMasAnnotTypeText() {
    return this.masAnnotTypeText;
  }

  /**
   * Mas Annotation type from string.
   *
   * @param text text.
   * @return type.
   */
  public static MasAnnotType fromString(String text) {
    for (MasAnnotType masAnnotType : MasAnnotType.values()) {
      if (masAnnotType.masAnnotTypeText.equalsIgnoreCase(text)) {
        return masAnnotType;
      }
    }
    return UNKNOWN;
  }
}
