package gov.va.vro;

import gov.va.vro.model.mas.ClaimCondition;
import gov.va.vro.model.mas.ClaimDetail;
import gov.va.vro.model.mas.MasAnnotation;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasDocument;
import gov.va.vro.model.mas.VeteranIdentifiers;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;

import java.util.Collections;
import java.util.UUID;

public class MasTestData {

  public static MasAutomatedClaimPayload getMasAutomatedClaimPayload() {
    return getMasAutomatedClaimPayload(123, "7101", "999");
  }

  public static MasAutomatedClaimRequest getMasAutomatedClaimRequest() {
    return getMasAutomatedClaimRequest(123, "1233", "999");
  }

  /**
   * Gets the automated claim payload.
   *
   * @param collectionId collection ID.
   * @param diagnosticCode diagnostic code.
   * @param claimId claim ID.
   * @return Claim payload.
   */
  public static MasAutomatedClaimPayload getMasAutomatedClaimPayload(
      int collectionId, String diagnosticCode, String claimId) {
    VeteranIdentifiers veteranIdentifiers = getVeteranIdentifiers();
    ClaimDetail claimDetail = getClaimDetail(diagnosticCode, claimId);

    return MasAutomatedClaimPayload.builder()
        .dateOfBirth("2002-12-12")
        .collectionId(collectionId)
        .firstName("Rick")
        .lastName("Smith")
        .veteranIdentifiers(veteranIdentifiers)
        .claimDetail(claimDetail)
        .correlationId(UUID.randomUUID().toString())
        .build();
  }

  public static MasAutomatedClaimRequest getMasAutomatedClaimRequest(
      int collectionId, String diagnosticCode, String claimId) {
    VeteranIdentifiers veteranIdentifiers = getVeteranIdentifiers();
    ClaimDetail claimDetail = getClaimDetail(diagnosticCode, claimId);

    return MasAutomatedClaimRequest.builder()
        .dateOfBirth("2002-12-12")
        .collectionId(collectionId)
        .firstName("Rick")
        .lastName("Smith")
        .veteranIdentifiers(veteranIdentifiers)
        .claimDetail(claimDetail)
        .build();
  }

  private static ClaimDetail getClaimDetail(String diagnosticCode, String claimId) {
    ClaimCondition conditions = new ClaimCondition();
    conditions.setDiagnosticCode(diagnosticCode);
    conditions.setDisabilityActionType("INCREASE");
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setClaimSubmissionDateTime("2022-02-04T17:45:59Z");
    claimDetail.setConditions(conditions);
    claimDetail.setBenefitClaimId(claimId);
    return claimDetail;
  }

  private static VeteranIdentifiers getVeteranIdentifiers() {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId("X");
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    return veteranIdentifiers;
  }

  /**
   * Creates hypertension document.
   *
   * @return the document.
   */
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
