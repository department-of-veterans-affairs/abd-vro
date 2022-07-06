package gov.va.vro.service.db.mapper;

import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.starter.example.service.spi.db.model.Contention;
import gov.va.starter.example.service.spi.db.model.Veteran;
import gov.va.vro.persistence.model.ClaimEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClaimMapperTest {

  private ClaimRequestMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(ClaimRequestMapper.class);
  }

  @Test
  void toEntity() {
    Claim claim = new Claim();
    claim.setClaimId("claim1");
    Veteran veteran = new Veteran();
    veteran.setIcn("v1");
    veteran.setParticipantId("p");
    Contention contention1 = new Contention();
    contention1.setDiagnosticCode("1233");
    contention1.addAssessmentResult(2);
    contention1.addEvidenceSummaryDocument("doc1", 1);
    contention1.addEvidenceSummaryDocument("doc2", 2);

    claim.getContentions().add(contention1);
    claim.setVeteran(veteran);
    ClaimEntity entity = mapper.toClaimEntity(claim);
    // TODO: verify
    System.out.println(entity);
  }
}
