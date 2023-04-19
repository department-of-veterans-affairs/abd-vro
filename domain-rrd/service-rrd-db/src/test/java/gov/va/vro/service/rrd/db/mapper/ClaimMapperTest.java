package gov.va.vro.service.rrd.db.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.service.spi.model.Claim;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClaimMapperTest {

  @Test
  void toClaim() {
    VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setIcn("icn2");
    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setVbmsId("123");
    claimEntity.setVeteran(veteranEntity);
    ContentionEntity contentionEntity1 = new ContentionEntity();
    contentionEntity1.setDiagnosticCode("7101");
    claimEntity.addContention(contentionEntity1);
    ContentionEntity contentionEntity2 = new ContentionEntity();
    contentionEntity2.setDiagnosticCode("6202");
    claimEntity.addContention(contentionEntity2);

    ClaimMapper claimMapper = Mappers.getMapper(ClaimMapper.class);
    Claim claim = claimMapper.toClaim(claimEntity);
    assertEquals(claimEntity.getVbmsId(), claim.getBenefitClaimId());
    assertEquals(claimEntity.getVeteran().getIcn(), claim.getVeteranIcn());
    assertEquals(2, claim.getContentions().size());
    assertTrue(claim.getContentions().contains("7101"));
    assertTrue(claim.getContentions().contains("6202"));
  }
}
