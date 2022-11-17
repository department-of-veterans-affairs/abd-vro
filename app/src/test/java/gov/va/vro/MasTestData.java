package gov.va.vro;

import gov.va.vro.model.mas.ClaimCondition;
import gov.va.vro.model.mas.ClaimDetail;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.VeteranIdentifiers;

public class MasTestData {

  public static MasAutomatedClaimPayload getMasAutomatedClaimPayload() {
    return getMasAutomatedClaimPayload(123);
  }

  public static MasAutomatedClaimPayload getMasAutomatedClaimPayload(int collectionId) {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId("X");
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    ClaimCondition conditions = new ClaimCondition();
    conditions.setDiagnosticCode("1233");
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setClaimSubmissionDateTime("123");
    claimDetail.setConditions(conditions);

    return MasAutomatedClaimPayload.builder()
        .dateOfBirth("2002-12-12")
        .collectionId(collectionId)
        .firstName("Rick")
        .lastName("Smith")
        .veteranIdentifiers(veteranIdentifiers)
        .claimDetail(claimDetail)
        .build();
  }
}
