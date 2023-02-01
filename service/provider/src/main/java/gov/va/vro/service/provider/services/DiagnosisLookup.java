package gov.va.vro.service.provider.services;

import java.util.Map;

public final class DiagnosisLookup {

  private DiagnosisLookup() {}

  private static final Map<String, String> diagnosisMap =
      Map.of("7101", "Hypertension", "6602", "Asthma", "1233", "1233");

  public static String getDiagnosis(String diagnosticCode) {
    return diagnosisMap.get(diagnosticCode);
  }
}
