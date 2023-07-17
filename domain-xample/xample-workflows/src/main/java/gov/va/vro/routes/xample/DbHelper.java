package gov.va.vro.routes.xample;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.ContentionEventEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ContentionEventRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbHelper {

  @Autowired private final ClaimRepository claimRepository;
  @Autowired private final VeteranRepository veteranRepository;
  @Autowired private final ContentionEventRepository contentionEventRepository;

  public ClaimEntity saveToDb(final SomeDtoModel myModel) {
    final VeteranEntity veteranEntity = createVeteran(myModel.getResourceId() + "-vet", null);

    final ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setVeteran(veteranEntity);
    claimEntity.setVbmsId(myModel.getResourceId());

    final ContentionEntity contentionEntity = new ContentionEntity();
    contentionEntity.setDiagnosticCode(myModel.getDiagnosticCode());
    claimEntity.addContention(contentionEntity);

    claimRepository.save(claimEntity);
    log.info("Saved {}", claimEntity);
    return claimEntity;
  }

  private VeteranEntity createVeteran(final String veteranIcn, final String veteranParticipantId) {
    final VeteranEntity veteranEntity = new VeteranEntity();
    veteranEntity.setIcn(veteranIcn);
    veteranEntity.setParticipantId(veteranParticipantId);
    return veteranRepository.save(veteranEntity);
  }

  public ContentionEventEntity saveContentionEvent(final BieMessagePayload bieMessagePayload) {
    final ContentionEventEntity contentionEventEntity = new ContentionEventEntity();
    contentionEventEntity.setEventType(bieMessagePayload.getEvent());
    contentionEventEntity.setEventDetails(bieMessagePayload.getEventDetails());
    contentionEventEntity.setNotifiedAt(LocalDateTime.parse(bieMessagePayload.getNotifiedAt()));
    return contentionEventRepository.save(contentionEventEntity);
  }
}
