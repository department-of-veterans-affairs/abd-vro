package gov.va.vro.service.provider.bgs.service;

import static gov.va.vro.service.provider.bgs.service.BgsClaimNotes.OFFRAMP_ERROR_2_CLAIM_NOTE;
import static gov.va.vro.service.provider.bgs.service.BgsVeteranNote.getArsdUploadedNote;

import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.provider.mas.MasCompletionStatus;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BgsApiClient {
  private final EvidenceSummaryDocumentRepository esDocRepository;

  private int retryDelayBaseMills = 130_000;

  public BgsNotesCamelBody buildRequest(MasProcessingObject mpo) {
    BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, retryDelayBaseMills);
    MasCompletionStatus completionStatus = MasCompletionStatus.of(mpo);
    switch (completionStatus) {
      case READY_FOR_DECISION:
        // Tested with VroV2Tests.testAutomatedClaimFullPositiveIncrease
        log.warn("++++++++ RFD +++++++");
        body.pendingRequests.add(buildVeteranNoteRequest(mpo));
        body.pendingRequests.add(
            buildClaimNotesRequest(mpo, BgsClaimNotes.RFD_NOTE, BgsClaimNotes.ARSD_COMPLETED_NOTE));
        break;
      case EXAM_ORDER:
        // Tested with VroV2Tests.testAutomatedClaimOrderExamNewClaim
        log.warn("++++++++ EXAM_ORDER +++++++");
        body.pendingRequests.add(buildVeteranNoteRequest(mpo));
        body.pendingRequests.add(buildClaimNotesRequest(mpo, BgsClaimNotes.EXAM_REQUESTED_NOTE));
        break;
      case OFF_RAMP:
        // Tested with VroV2Tests.testAutomatedClaimSufficiencyIsNull,
        // which only covers SUFFICIENCY_UNDETERMINED
        var offRampError = mpo.getOffRampReason();
        log.warn("++++++++ offRampError=" + offRampError);
        String claimNote = OFFRAMP_ERROR_2_CLAIM_NOTE.getOrDefault(offRampError, null);
        log.warn("++++++++ claimNote: " + claimNote);
        if (claimNote != null) body.pendingRequests.add(buildClaimNotesRequest(mpo, claimNote));
        break;
    }
    return body;
  }

  private BgsApiClientRequest buildVeteranNoteRequest(MasProcessingObject mpo) {
    String veteranId = mpo.getClaimPayload().getVeteranIdentifiers().getParticipantId();
    var request = new BgsApiClientRequest(mpo.getBenefitClaimId(), veteranId);
    request.veteranNote = getArsdUploadedNote(getDocUploadedAt(mpo));
    return request;
  }

  private BgsApiClientRequest buildClaimNotesRequest(
      MasProcessingObject mpo, String... claimsNotes) {
    var request = new BgsApiClientRequest(mpo.getBenefitClaimId(), null);
    request.claimNotes.addAll(Arrays.asList(claimsNotes));
    return request;
  }

  private OffsetDateTime getDocUploadedAt(MasProcessingObject mpo) {
    var doc =
        getEvidenceSummaryDocumentEntity(mpo.getClaimPayload().getEvidenceSummaryDocumentId());
    OffsetDateTime docUploadedAt = doc.getUploadedAt();
    return docUploadedAt;
  }

  @NotNull
  private EvidenceSummaryDocumentEntity getEvidenceSummaryDocumentEntity(UUID uuid) {
    Optional<EvidenceSummaryDocumentEntity> foundDoc = esDocRepository.findById(uuid);
    if (foundDoc.isEmpty()) {
      log.error("Could not find EvidenceSummaryDocumentEntity with uuid {}.", uuid);
      throw new EntityNotFoundException("EvidenceSummaryDocumentEntity", uuid);
    }
    return foundDoc.get();
  }
}
