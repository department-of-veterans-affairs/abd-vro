package gov.va.vro.service.provider.bgs.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gov.va.vro.model.rrd.bgs.BgsApiClientRequest;
import gov.va.vro.model.rrd.event.EventReason;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.provider.MasProcessingObjectTestData;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class BgsApiClientTest {

  static EvidenceSummaryDocumentRepository esDocRepository;
  static BgsApiClient client;

  @BeforeAll
  static void setupStatics() {
    esDocRepository = Mockito.mock(EvidenceSummaryDocumentRepository.class);
    // The client is stateless, so a single instance can handle multiple tests
    client = new BgsApiClient(esDocRepository);
  }

  static final OffsetDateTime uploadedAt = OffsetDateTime.now();

  private void mockEsdRepoFindById() {
    EvidenceSummaryDocumentEntity entity = new EvidenceSummaryDocumentEntity();
    entity.setUploadedAt(uploadedAt);
    when(esDocRepository.findById(any())).thenReturn(Optional.of(entity));
  }

  private MasProcessingObject mpoWithMasCamelStage(MasCamelStage stage) {
    return MasProcessingObjectTestData.builder().masCamelStage(stage).build().create();
  }

  @Test
  void buildRequestTest_ReadyForDecision() {
    mockEsdRepoFindById();
    var mpo = mpoWithMasCamelStage(MasCamelStage.DURING_PROCESSING);
    mpo.setSufficientForFastTracking(true);
    var requests = client.buildRequest(mpo).getPendingRequests();

    // Should be 2 separate requests -- one for veteran note, another for claim notes
    assertEquals(2, requests.size());
    requests.forEach(request -> assertTrue(request.isConstraintSatisfied()));

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
    mockEsdRepoFindById();
    var mpo = mpoWithMasCamelStage(MasCamelStage.DURING_PROCESSING);
    mpo.setSufficientForFastTracking(false);
    var requests = client.buildRequest(mpo).getPendingRequests();

    // Should be 2 separate requests -- one for veteran note, another for claim notes
    assertEquals(2, requests.size());
    requests.forEach(request -> assertTrue(request.isConstraintSatisfied()));

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

  MasProcessingObject mpoWithOfframpReason(EventReason reason) {
    var mpo = mpoWithMasCamelStage(masCamelStageForOfframp());
    mpo.getClaimPayload().setOffRampReason(reason.getCode());
    return mpo;
  }

  private static final Random random = new Random();

  MasCamelStage masCamelStageForOfframp() {
    // stage doesn't matter as long as there is an setOffRampReason
    MasCamelStage[] stages = {MasCamelStage.START_COMPLETE, MasCamelStage.DURING_PROCESSING};
    return stages[random.nextInt(stages.length)];
  }
}
