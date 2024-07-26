package gov.va.vro.routes.xample;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.persistence.model.ContentionEventEntity;
import gov.va.vro.persistence.repository.ContentionEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbHelper {

  @Autowired private final ContentionEventRepository contentionEventRepository;

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
    contentionEventEntity.setOccurredAt(convertTime(bieMessagePayload.getEventTime()));
    contentionEventEntity.setDateAdded(convertTime(bieMessagePayload.getDateAdded()));
    contentionEventEntity.setDateCompleted(convertTime(bieMessagePayload.getDateCompleted()));
    contentionEventEntity.setDateUpdated(convertTime(bieMessagePayload.getDateUpdated()));
    contentionEventEntity.setActorStation(bieMessagePayload.getActorStation());
    contentionEventEntity.setActorUserId(bieMessagePayload.getActorUserId());
    contentionEventEntity.setAutomationIndicator(
        Optional.ofNullable(bieMessagePayload.getAutomationIndicator()).orElse(false));
    contentionEventEntity.setBenefitClaimTypeCode(bieMessagePayload.getBenefitClaimTypeCode());
    contentionEventEntity.setContentionStatusTypeCode(
        bieMessagePayload.getContentionStatusTypeCode());
    contentionEventEntity.setCurrentLifecycleStatus(bieMessagePayload.getCurrentLifecycleStatus());
    contentionEventEntity.setDetails(bieMessagePayload.getDetails());
    contentionEventEntity.setEventTime(convertTime(bieMessagePayload.getEventTime()));
    contentionEventEntity.setJournalStatusTypeCode(bieMessagePayload.getJournalStatusTypeCode());
    contentionEventEntity.setVeteranParticipantId(bieMessagePayload.getVeteranParticipantId());
    contentionEventEntity.setActionName(bieMessagePayload.getActionName());
    contentionEventEntity.setActionResultName(bieMessagePayload.getActionResultName());
    contentionEventEntity.setDescription(bieMessagePayload.getDescription());

    contentionEventRepository.save(contentionEventEntity);
    log.info("Saved bieMessagePayload {}", bieMessagePayload);
    return contentionEventEntity;
  }

  public LocalDateTime convertTime(Long time) {
    if (time == null) return null;

    return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
  }
}
