package gov.va.vro.service.provider;

import gov.va.vro.model.rrd.mas.ClaimDetail;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.rrd.mas.VeteranIdentifiers;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.Builder;

// TODO: merge with MasTestData
// TODO: refactor tests to use the resulting class to minimize duplicate code and facilitate changes
@Builder(toBuilder = true)
public class MasProcessingObjectTestData {
  @Builder.Default int collectionId = 20230420;
  @Builder.Default String claimId = "0613";
  @Builder.Default String veteranParticipantId = "vetPid20230421";

  @Builder.Default MasCamelStage masCamelStage = MasCamelStage.START_COMPLETE;

  public MasProcessingObject create() {
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setBenefitClaimId(claimId);
    VeteranIdentifiers veteranIdentifiers = createVeteranIdentifiers();
    MasAutomatedClaimPayload claimPayload =
        MasAutomatedClaimPayload.builder()
            .collectionId(collectionId)
            .claimDetail(claimDetail)
            .veteranIdentifiers(veteranIdentifiers)
            .build();
    return new MasProcessingObject(claimPayload, masCamelStage);
  }

  public VeteranIdentifiers createVeteranIdentifiers() {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId(veteranParticipantId);
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    return veteranIdentifiers;
  }
}
