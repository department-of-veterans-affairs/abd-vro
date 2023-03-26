package gov.va.vro.service.rrd.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.rrd.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.ClaimsInfo;
import gov.va.vro.model.rrd.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.rrd.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.rrd.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.model.rrd.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.service.rrd.db.util.ClaimMetricsTestCase;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.ExamOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@EnableJpaAuditing
public class ClaimMetricsServiceImplTest {

  @Autowired private ClaimMetricsServiceImpl claimMetricsService;

  @Autowired private SaveToDbServiceImpl saveToDbService;

  @Autowired private ClaimRepository claimRepository;

  @Autowired private ClaimSubmissionRepository claimSubmissionRepository;

  private void verifyFindAllClaimInfo(
      ClaimInfoQueryParams params, List<ClaimMetricsTestCase> cases) {
    int size = params.getSize();
    int page = params.getPage();
    assertTrue(size > 0);
    ClaimsInfo claimsInfo = claimMetricsService.findAllClaimInfo(params);
    assertEquals(cases.size(), claimsInfo.getTotal());
    List<ClaimInfoResponse> responses = claimsInfo.getClaimInfoList();
    int expectedResponseSize =
        cases.size() < (page + 1) * size ? (page + 1) * size - cases.size() : size;
    assertEquals(expectedResponseSize, responses.size());
    IntStream.range(0, expectedResponseSize)
        .forEach(
            index -> {
              ClaimInfoResponse cir = responses.get(index);
              ClaimMetricsTestCase c = cases.get(index + page * size);
              c.verifyClaimInfoResponse(cir);
            });
  }

  private void verifyHappyPathClaimMetrics(int expectedSize) {
    ClaimMetricsResponse actual = claimMetricsService.getClaimMetrics();
    ClaimMetricsResponse expected =
        new ClaimMetricsResponse(expectedSize, expectedSize, expectedSize);
    assertEquals(expected, actual);
  }

  @Test
  void testAllMethodsHappyPath() {
    List<ClaimMetricsTestCase> cases =
        IntStream.range(0, 15).boxed().map(i -> ClaimMetricsTestCase.getInstance()).toList();

    List<ClaimMetricsTestCase> secondClaimCases =
        cases.stream().limit(5).map(ClaimMetricsTestCase::newCaseForSameVeteran).toList();
    List<ClaimMetricsTestCase> thirdClaimCases =
        cases.stream().limit(2).map(ClaimMetricsTestCase::newCaseForSameVeteran).toList();

    List<ClaimMetricsTestCase> allCases = new ArrayList<>(cases);
    allCases.addAll(secondClaimCases);
    allCases.addAll(thirdClaimCases);

    verifyHappyPathClaimMetrics(0);
    allCases.forEach(c -> c.populate(saveToDbService, claimSubmissionRepository));
    verifyHappyPathClaimMetrics(22);

    // The test populate method uses v1 for the claim id Type
    allCases.forEach(
        c -> {
          String claimSubmissionId = c.getClaimSubmissionId();
          ClaimInfoResponse cir =
              claimMetricsService.findClaimInfo(claimSubmissionId, Claim.V1_ID_TYPE);
          c.verifyClaimInfoResponse(cir);
        });
    // Reverse the icnCases to get the last updated claims for that ICN.
    List<ClaimMetricsTestCase> icnCases =
        new ArrayList<>(IntStream.of(1, 16, 21).boxed().map(allCases::get).toList());
    String icn = allCases.get(21).getIcn();

    // We expect the results to be in order of last updated.
    Collections.reverse(icnCases);
    ClaimInfoQueryParams params0 = ClaimInfoQueryParams.builder().size(2).icn(icn).build();
    verifyFindAllClaimInfo(params0, icnCases);

    ClaimInfoQueryParams params1 = ClaimInfoQueryParams.builder().page(1).size(2).icn(icn).build();
    verifyFindAllClaimInfo(params1, icnCases);

    // We expect the results to be in order of last updated.
    Collections.reverse(allCases);

    ClaimInfoQueryParams params2 = ClaimInfoQueryParams.builder().build();
    verifyFindAllClaimInfo(params2, allCases);

    ClaimInfoQueryParams params3 = ClaimInfoQueryParams.builder().size(7).page(1).build();
    verifyFindAllClaimInfo(params3, allCases);
  }

  @Test
  void testFindClaimInfoInvalidId() {
    // Put something in the database so that it is not empty
    ClaimMetricsTestCase testCase = ClaimMetricsTestCase.getInstance();
    testCase.populate(saveToDbService, claimSubmissionRepository);

    ClaimInfoResponse cir = claimMetricsService.findClaimInfo("not_id", Claim.V1_ID_TYPE);
    assertNull(cir);
  }

  @Test
  void testFindExamOrderInfo() {
    OffsetDateTime examTime = OffsetDateTime.now();
    ExamOrder testExamOrder = new ExamOrder();
    testExamOrder.setCollectionId("123");
    testExamOrder.setIdType(MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
    testExamOrder.setExamOrderDateTime(examTime);
    saveToDbService.insertOrUpdateExamOrderingStatus(testExamOrder);

    ExamOrder examOrderNoTimeStamp = new ExamOrder();
    examOrderNoTimeStamp.setCollectionId("124");
    examOrderNoTimeStamp.setIdType(MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
    saveToDbService.insertOrUpdateExamOrderingStatus(examOrderNoTimeStamp);

    ExamOrderInfoQueryParams params = ExamOrderInfoQueryParams.builder().build();
    ExamOrdersInfo eoir = claimMetricsService.findAllExamOrderInfo(params);
    List<ExamOrderInfoResponse> examOrderResponses = eoir.getExamOrderInfoList();

    assertEquals(2, examOrderResponses.size());
    assertEquals(
        examOrderNoTimeStamp.getCollectionId(), examOrderResponses.get(0).getCollectionId());
    assertNull(examOrderResponses.get(0).getOrderedAt());
    assertFalse(examOrderResponses.get(0).isHasAssociatedClaimSubmission());
    assertEquals(testExamOrder.getCollectionId(), examOrderResponses.get(1).getCollectionId());
    assertNotNull(examOrderResponses.get(1).getOrderedAt());
    assertFalse(examOrderResponses.get(0).isHasAssociatedClaimSubmission());
  }
}
