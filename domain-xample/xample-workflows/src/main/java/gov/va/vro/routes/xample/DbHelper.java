package gov.va.vro.routes.xample;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbHelper {

  @Autowired private final ClaimRepository claimRepository;
  @Autowired private final VeteranRepository veteranRepository;

  public ClaimEntity saveToDb(SomeDtoModel myModel) {
    var veteranEntity = createVeteran(myModel.getResourceId() + "-vet", null);

    var claimEntity = new ClaimEntity();
    claimEntity.setVeteran(veteranEntity);
    claimEntity.setVbmsId(myModel.getResourceId());

    ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(myModel.getDiagnosticCode());
    claimEntity.addContention(contentionEntity);

    claimRepository.save(claimEntity);
    log.info("Saved {}", claimEntity);
    return claimEntity;
  }

  private VeteranEntity createVeteran(String veteranIcn, String veteranParticipantId) {
    VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setIcn(veteranIcn);
    veteranEntity.setParticipantId(veteranParticipantId);
    return veteranRepository.save(veteranEntity);
  }
}
