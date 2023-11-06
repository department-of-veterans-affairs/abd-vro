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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    contentionEventEntity.setClaimId(bieMessagePayload.getClaimId());
    contentionEventEntity.setContentionId(bieMessagePayload.getContentionId());
    contentionEventEntity.setEventType(bieMessagePayload.getEventType().toString());
    contentionEventEntity.setNotifiedAt(convertTime(bieMessagePayload.getNotifiedAt()));
    contentionEventEntity.setContentionTypeCode(bieMessagePayload.getContentionTypeCode());
    contentionEventEntity.setDiagnosticTypeCode(bieMessagePayload.getDiagnosticTypeCode());
    contentionEventEntity.setContentionClassificationName(
        bieMessagePayload.getContentionClassificationName());
    contentionEventEntity.setOccurredAt(convertTime(bieMessagePayload.getOccurredAt()));
    contentionEventEntity.setDateAdded(convertTime(bieMessagePayload.getDateAdded()));
    contentionEventEntity.setDateCompleted(convertTime(bieMessagePayload.getDateCompleted()));
    contentionEventEntity.setDateUpdated(convertTime(bieMessagePayload.getDateUpdated()));
    contentionEventEntity.setActorStation(bieMessagePayload.getActorStation());
    contentionEventEntity.setAutomationIndicator(bieMessagePayload.isAutomationIndicator());
    contentionEventEntity.setBenefitClaimTypeCode(bieMessagePayload.getBenefitClaimTypeCode());
    contentionEventEntity.setContentionStatusTypeCode(bieMessagePayload.getContentionStatusTypeCode());
    contentionEventEntity.setCurrentLifecycleStatus(bieMessagePayload.getCurrentLifecycleStatus());
    contentionEventEntity.setDetails(bieMessagePayload.getDetails());
    contentionEventEntity.setEventTime(convertTime(bieMessagePayload.getEventTime()));
    contentionEventEntity.setJournalStatusTypeCode(bieMessagePayload.getJournalStatusTypeCode());
    contentionEventEntity.setVeteranParticipantId(bieMessagePayload.getVeteranParticipantId());

    return contentionEventRepository.save(contentionEventEntity);
  }

  public LocalDateTime convertTime(long time) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
  }
}
