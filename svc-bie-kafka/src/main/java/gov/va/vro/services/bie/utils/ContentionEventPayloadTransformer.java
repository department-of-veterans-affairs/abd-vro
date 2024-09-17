package gov.va.vro.services.bie.utils;

import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.persistence.model.bieevents.ContentionEventEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class ContentionEventPayloadTransformer {

  public ContentionEventEntity toContentionEventEntity(
      final ContentionEventPayload contentionEventPayload) {
    final ContentionEventEntity contentionEventEntity = new ContentionEventEntity();
    contentionEventEntity.setClaimId(contentionEventPayload.getClaimId());
    contentionEventEntity.setContentionId(contentionEventPayload.getContentionId());
    contentionEventEntity.setEventType(contentionEventPayload.getEventType().toString());
    contentionEventEntity.setNotifiedAt(convertTime(contentionEventPayload.getNotifiedAt()));
    contentionEventEntity.setContentionTypeCode(contentionEventPayload.getContentionTypeCode());
    contentionEventEntity.setDiagnosticTypeCode(contentionEventPayload.getDiagnosticTypeCode());
    contentionEventEntity.setContentionClassificationName(
        contentionEventPayload.getContentionClassificationName());
    contentionEventEntity.setOccurredAt(convertTime(contentionEventPayload.getEventTime()));
    contentionEventEntity.setDateAdded(convertTime(contentionEventPayload.getDateAdded()));
    contentionEventEntity.setDateCompleted(convertTime(contentionEventPayload.getDateCompleted()));
    contentionEventEntity.setDateUpdated(convertTime(contentionEventPayload.getDateUpdated()));
    contentionEventEntity.setActorStation(contentionEventPayload.getActorStation());
    contentionEventEntity.setActorUserId(contentionEventPayload.getActorUserId());
    contentionEventEntity.setAutomationIndicator(
        Optional.ofNullable(contentionEventPayload.getAutomationIndicator()).orElse(false));
    contentionEventEntity.setBenefitClaimTypeCode(contentionEventPayload.getBenefitClaimTypeCode());
    contentionEventEntity.setContentionStatusTypeCode(
        contentionEventPayload.getContentionStatusTypeCode());
    contentionEventEntity.setCurrentLifecycleStatus(
        contentionEventPayload.getCurrentLifecycleStatus());
    contentionEventEntity.setDetails(contentionEventPayload.getDetails());
    contentionEventEntity.setEventTime(convertTime(contentionEventPayload.getEventTime()));
    contentionEventEntity.setJournalStatusTypeCode(
        contentionEventPayload.getJournalStatusTypeCode());
    contentionEventEntity.setVeteranParticipantId(contentionEventPayload.getVeteranParticipantId());
    contentionEventEntity.setActionName(contentionEventPayload.getActionName());
    contentionEventEntity.setActionResultName(contentionEventPayload.getActionResultName());
    contentionEventEntity.setDescription(contentionEventPayload.getDescription());

    return contentionEventEntity;
  }

  private static LocalDateTime convertTime(Long time) {
    if (time == null) return null;

    return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
  }
}
