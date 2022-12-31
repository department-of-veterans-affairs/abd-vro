package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.db.util.ClaimMetricsTestCase;
import gov.va.vro.service.db.util.ServiceBundle;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
public class ClaimMetricsServiceImplTest {

  @Autowired private ClaimMetricsServiceImpl claimMetricsService;

  @Autowired private SaveToDbServiceImpl saveToDbService;

  @Autowired private ClaimRepository claimRepository;

  private void verifyFindAllClaimInfo(
      ClaimInfoQueryParams params, List<ClaimMetricsTestCase> cases) {
    int size = params.getSize();
    int page = params.getPage();
    assertTrue(size > 0);
    List<ClaimInfoResponse> responses = claimMetricsService.findAllClaimInfo(params);
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

  @Test
  void testAllMethodsHappyPath() {
    ServiceBundle bundle = new ServiceBundle(claimMetricsService, saveToDbService, claimRepository);

    Supplier f = () -> ClaimMetricsTestCase.getInstance(bundle);

    List<ClaimMetricsTestCase> cases =
        IntStream.range(0, 15).boxed().map(i -> ClaimMetricsTestCase.getInstance(bundle)).toList();

    List<ClaimMetricsTestCase> secondClaimCases =
        cases.stream().limit(5).map(c -> c.newCaseForSameVeteran(bundle)).toList();
    List<ClaimMetricsTestCase> thirdClaimCases =
        cases.stream().limit(2).map(c -> c.newCaseForSameVeteran(bundle)).toList();

    ArrayList<ClaimMetricsTestCase> allCases = new ArrayList<>(cases);
    allCases.addAll(secondClaimCases);
    allCases.addAll(thirdClaimCases);

    allCases.forEach(c -> c.populate());

    ClaimMetricsInfo metricsInfo = claimMetricsService.claimMetrics();
    assertEquals(allCases.size(), metricsInfo.getTotalClaims());
    assertEquals(allCases.size(), metricsInfo.getAssessmentResults());
    assertEquals(allCases.size(), metricsInfo.getEvidenceSummaryDocuments());

    allCases.forEach(
        c -> {
          String claimSubmissionId = c.getClaimSubmissionId();
          ClaimInfoResponse cir = claimMetricsService.getClaimInfo(claimSubmissionId);
          c.verifyClaimInfoResponse(cir);
        });

    ClaimInfoQueryParams params0 = ClaimInfoQueryParams.builder().build();
    verifyFindAllClaimInfo(params0, allCases);

    ClaimInfoQueryParams params1 = ClaimInfoQueryParams.builder().size(7).page(1).build();
    verifyFindAllClaimInfo(params1, allCases);

    List<ClaimMetricsTestCase> icnCases =
        IntStream.of(1, 16, 21).boxed().map(index -> allCases.get(index)).toList();
    String icn = allCases.get(21).getIcn();

    ClaimInfoQueryParams params3 = ClaimInfoQueryParams.builder().size(2).icn(icn).build();
    verifyFindAllClaimInfo(params3, icnCases);

    ClaimInfoQueryParams params4 = ClaimInfoQueryParams.builder().page(1).size(2).icn(icn).build();
    verifyFindAllClaimInfo(params4, icnCases);
  }
}
