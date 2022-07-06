package gov.va.vro.service.db.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.starter.example.service.spi.db.model.Contention;
import gov.va.starter.example.service.spi.db.model.Veteran;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClaimMapperTest {

  private ClaimMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = Mappers.getMapper(ClaimMapper.class);
  }

  @Test
  void toEntity() {
    Claim claim = new Claim();
    claim.setClaimId("claim1");
    Veteran veteran = new Veteran();
    veteran.setIcn("v1");
    veteran.setParticipantId("p");
    Contention contention = new Contention();
    contention.setDiagnosticCode("1233");
    contention.addAssessmentResult(2);
    contention.addEvidenceSummaryDocument("doc1", 1);
    contention.addEvidenceSummaryDocument("doc2", 2);
    claim.getContentions().add(contention);
    claim.setVeteran(veteran);
    ClaimEntity claimEntity = mapper.toClaimEntity(claim);
    assertEquals(claim.getClaimId(), claimEntity.getClaimId());
    assertEquals(claim.getIdType(), claimEntity.getIdType());
    VeteranEntity veteranEntity = claimEntity.getVeteran();
    assertEquals(veteran.getIcn(), veteranEntity.getIcn());
    assertEquals(veteran.getParticipantId(), veteranEntity.getParticipantId());
    ContentionEntity contentionEntity = claimEntity.getContentions().get(0);
    assertEquals(contention.getDiagnosticCode(), contentionEntity.getDiagnosticCode());
  }
}
