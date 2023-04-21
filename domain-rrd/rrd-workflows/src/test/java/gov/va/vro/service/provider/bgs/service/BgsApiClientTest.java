package gov.va.vro.service.provider.bgs.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gov.va.vro.model.rrd.bgs.BgsApiClientRequest;
import gov.va.vro.model.rrd.event.EventReason;
import gov.va.vro.model.rrd.mas.ClaimDetail;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.rrd.mas.VeteranIdentifiers;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class BgsApiClientTest {

  @Mock EvidenceSummaryDocumentRepository esDocRepository;
  BgsApiClient client;

  @BeforeEach
  void setup() {
    client = new BgsApiClient(esDocRepository);
  }

  static final String claimId = "0613";
  static final String participantId = "vetPid20230421";

  static final OffsetDateTime uploadedAt = OffsetDateTime.now();

  void mockRepoFindById() {
    EvidenceSummaryDocumentEntity entity = new EvidenceSummaryDocumentEntity();
    entity.setUploadedAt(uploadedAt);
    Optional<EvidenceSummaryDocumentEntity> foundDoc = Optional.of(entity);
    when(esDocRepository.findById(any())).thenReturn(foundDoc);
  }

  // TODO: move this method to a factory class for tests to use
  static MasProcessingObject createMasPayload(MasCamelStage stage) {
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setBenefitClaimId(claimId);
    VeteranIdentifiers veteranIdentifiers = createVeteranIdentifiers();
    MasAutomatedClaimPayload claimPayload =
        MasAutomatedClaimPayload.builder()
            .claimDetail(claimDetail)
            .veteranIdentifiers(veteranIdentifiers)
            .build();
    return new MasProcessingObject(claimPayload, stage);
  }

  // TODO: move this method to a factory class for tests to use
  static VeteranIdentifiers createVeteranIdentifiers() {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId(participantId);
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    return veteranIdentifiers;
  }

  @Test
  void buildRequestTest_ReadyForDecision() {
    mockRepoFindById();
    var mpo = createMasPayload(MasCamelStage.DURING_PROCESSING);
    mpo.setSufficientForFastTracking(true);
    var requests = client.buildRequest(mpo).getPendingRequests();

    assertEquals(2, requests.size());

    var claimNotes = extractClaimNotesFrom(requests);
    assertThat(claimNotes)
        .hasSize(2)
        .containsExactlyInAnyOrder(BgsClaimNotes.RFD_NOTE, BgsClaimNotes.ARSD_COMPLETED_NOTE);

    var veteranNotes = extractVeteranNotesFrom(requests);
    assertEquals(1, veteranNotes.size());
    var expectedVeteranNote = BgsVeteranNote.getArsdUploadedNote(uploadedAt);
    assertEquals(expectedVeteranNote, veteranNotes.get(0));
  }

  @Test
  void buildRequestTest_ExamOrder() {
    mockRepoFindById();
    var mpo = createMasPayload(MasCamelStage.DURING_PROCESSING);
    mpo.setSufficientForFastTracking(false);
    var requests = client.buildRequest(mpo).getPendingRequests();

    assertEquals(2, requests.size());

    var claimNotes = extractClaimNotesFrom(requests);
    assertThat(claimNotes).hasSize(1).containsExactlyInAnyOrder(BgsClaimNotes.EXAM_REQUESTED_NOTE);

    var veteranNotes = extractVeteranNotesFrom(requests);
    assertEquals(1, veteranNotes.size());
    var expectedVeteranNote = BgsVeteranNote.getArsdUploadedNote(uploadedAt);
    assertEquals(expectedVeteranNote, veteranNotes.get(0));
  }

  @NotNull
  private static List<String> extractClaimNotesFrom(List<BgsApiClientRequest> requests) {
    return requests.stream()
        .flatMap(request -> request.getClaimNotes().stream())
        .collect(Collectors.toList());
  }

  @NotNull
  private static List<String> extractVeteranNotesFrom(List<BgsApiClientRequest> requests) {
    return requests.stream()
        .map(request -> request.getVeteranNote())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Test
  void buildRequestTest_NewButNotPresumptive() {
    testClaimNotesForOffRampReason(
        EventReason.NEW_NOT_PRESUMPTIVE, BgsClaimNotes.CANT_CONFIRM_PRESUMPTIVE_NOTE);
  }

  @Test
  void buildRequestTest_SufficientUndetermined() {
    testClaimNotesForOffRampReason(
        EventReason.SUFFICIENCY_UNDETERMINED, BgsClaimNotes.CANT_CONFIRM_PRESUMPTIVE_NOTE);
  }

  @Test
  void buildRequestTest_PdfUploadFailed() {
    testClaimNotesForOffRampReason(
        EventReason.PDF_UPLOAD_FAILED_AFTER_ORDER_EXAM, BgsClaimNotes.ARSD_NOT_UPLOADED_NOTE);
  }

  void testClaimNotesForOffRampReason(EventReason reason, String expectedClaimNote) {
    var mpo = mpoWithOfframpReason(reason);
    var requests = client.buildRequest(mpo).getPendingRequests();

    assertEquals(1, requests.size());
    var claimNotes = requests.get(0).claimNotes;
    assertEquals(expectedClaimNote, claimNotes.get(0));
  }

  static MasProcessingObject mpoWithOfframpReason(EventReason reason) {
    var mpo = createMasPayload(masCamelStageForOfframp());
    mpo.getClaimPayload().setOffRampReason(reason.getCode());
    return mpo;
  }

  private static final Random random = new Random();

  static MasCamelStage masCamelStageForOfframp() {
    // stage doesn't matter as long as there is an setOffRampReason
    MasCamelStage[] stages = {MasCamelStage.START_COMPLETE, MasCamelStage.DURING_PROCESSING};
    return stages[random.nextInt(stages.length)];
  }
}
