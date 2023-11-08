package gov.va.vro.routes.xample;

import gov.va.vro.model.biekafka.BieMessageBasePayload;
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

  public ContentionEventEntity saveContentionEvent(final BieMessageBasePayload bieMessageBasePayload) {
    final ContentionEventEntity contentionEventEntity = new ContentionEventEntity();
    contentionEventEntity.setClaimId(bieMessageBasePayload.getClaimId());
    contentionEventEntity.setContentionId(bieMessageBasePayload.getContentionId());
    contentionEventEntity.setEventType(bieMessageBasePayload.getEventType().toString());
    contentionEventEntity.setNotifiedAt(convertTime(bieMessageBasePayload.getNotifiedAt()));
    contentionEventEntity.setContentionTypeCode(bieMessageBasePayload.getContentionTypeCode());
    contentionEventEntity.setDiagnosticTypeCode(bieMessageBasePayload.getDiagnosticTypeCode());
    contentionEventEntity.setContentionClassificationName(
        bieMessageBasePayload.getContentionClassificationName());
    contentionEventEntity.setOccurredAt(convertTime(bieMessageBasePayload.getOccurredAt()));
    contentionEventEntity.setDateAdded(convertTime(bieMessageBasePayload.getDateAdded()));
    contentionEventEntity.setDateCompleted(convertTime(bieMessageBasePayload.getDateCompleted()));
    contentionEventEntity.setDateUpdated(convertTime(bieMessageBasePayload.getDateUpdated()));
    contentionEventEntity.setActorStation(bieMessageBasePayload.getActorStation());
    contentionEventEntity.setAutomationIndicator(bieMessageBasePayload.isAutomationIndicator());
    contentionEventEntity.setBenefitClaimTypeCode(bieMessageBasePayload.getBenefitClaimTypeCode());
    contentionEventEntity.setContentionStatusTypeCode(bieMessageBasePayload.getContentionStatusTypeCode());
    contentionEventEntity.setCurrentLifecycleStatus(bieMessageBasePayload.getCurrentLifecycleStatus());
    contentionEventEntity.setDetails(bieMessageBasePayload.getDetails());
    contentionEventEntity.setEventTime(convertTime(bieMessageBasePayload.getEventTime()));
    contentionEventEntity.setJournalStatusTypeCode(bieMessageBasePayload.getJournalStatusTypeCode());
    contentionEventEntity.setVeteranParticipantId(bieMessageBasePayload.getVeteranParticipantId());

    return contentionEventRepository.save(contentionEventEntity);
  }

  public LocalDateTime convertTime(long time) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
  }
}
