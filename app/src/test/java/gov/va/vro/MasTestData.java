package gov.va.vro;

import gov.va.vro.model.mas.*;

import java.util.Collections;

public class MasTestData {

  public static MasAutomatedClaimPayload getMasAutomatedClaimPayload() {
    return getMasAutomatedClaimPayload(123, "1233");
  }

  public static MasAutomatedClaimPayload getMasAutomatedClaimPayload(
      int collectionId, String diagnosticCode) {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId("X");
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    ClaimCondition conditions = new ClaimCondition();
    conditions.setDiagnosticCode(diagnosticCode);
    conditions.setDisabilityActionType("INCREASE");
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setClaimSubmissionDateTime("2022-02-04");
    claimDetail.setConditions(conditions);
    claimDetail.setBenefitClaimId("999");

    return MasAutomatedClaimPayload.builder()
        .dateOfBirth("2002-12-12")
        .collectionId(collectionId)
        .firstName("Rick")
        .lastName("Smith")
        .veteranIdentifiers(veteranIdentifiers)
        .claimDetail(claimDetail)
        .build();
  }

  public static MasDocument createHypertensionDocument() {
    var document = new MasDocument();
    document.setCondition("Hypertension");
    var annotation = new MasAnnotation();
    annotation.setAnnotType("Medication");
    annotation.setAnnotVal("Placebo");
    document.setAnnotations(Collections.singletonList(annotation));
    return document;
  }
}
