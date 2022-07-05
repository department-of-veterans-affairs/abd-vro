package gov.va.vro.service.db.mapper;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.service.db.model.ClaimRequest;
import gov.va.vro.service.db.model.Contention;
import gov.va.vro.service.db.model.Veteran;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClaimRequestMapperTest {

  private ClaimRequestMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(ClaimRequestMapper.class);
  }

  @Test
  void toEntity() {
    ClaimRequest claimRequest = new ClaimRequest();
    claimRequest.setClaimId("claim1");
    Veteran veteran = new Veteran();
    veteran.setIcn("v1");
    veteran.setParticipantId("p");
    Contention contention1 = new Contention();
    contention1.setDiagnosticCode("1233");
    contention1.addAssessmentResult(2);
    contention1.addEvidenceSummaryDocument("doc1", 1);
    contention1.addEvidenceSummaryDocument("doc2", 2);

    claimRequest.getContentions().add(contention1);
    claimRequest.setVeteran(veteran);
    ClaimEntity entity = mapper.toClaimEntity(claimRequest);
    // TODO: verify
    System.out.println(entity);
  }
}
