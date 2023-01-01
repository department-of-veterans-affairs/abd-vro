package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.db.util.ClaimMetricsTestCase;
import gov.va.vro.model.claimmetrics.ClaimMetricsInfo;
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
    ClaimMetricsInfo actual = claimMetricsService.getClaimMetrics();
    ClaimMetricsInfo expected = new ClaimMetricsInfo(expectedSize, expectedSize, expectedSize);
    assertEquals(expected, actual);
  }

  @Test
  void testAllMethodsHappyPath() {
    Supplier<ClaimMetricsTestCase> f = () -> ClaimMetricsTestCase.getInstance();

    List<ClaimMetricsTestCase> cases =
        IntStream.range(0, 15).boxed().map(i -> ClaimMetricsTestCase.getInstance()).toList();

    List<ClaimMetricsTestCase> secondClaimCases =
        cases.stream().limit(5).map(c -> c.newCaseForSameVeteran()).toList();
    List<ClaimMetricsTestCase> thirdClaimCases =
        cases.stream().limit(2).map(c -> c.newCaseForSameVeteran()).toList();

    ArrayList<ClaimMetricsTestCase> allCases = new ArrayList<>(cases);
    allCases.addAll(secondClaimCases);
    allCases.addAll(thirdClaimCases);

    verifyHappyPathClaimMetrics(0);
    allCases.forEach(c -> c.populate(saveToDbService, claimRepository));
    verifyHappyPathClaimMetrics(22);

    allCases.forEach(
        c -> {
          String claimSubmissionId = c.getClaimSubmissionId();
          ClaimInfoResponse cir = claimMetricsService.findClaimInfo(claimSubmissionId);
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

  @Test
  void testFindClaimInfoInvalidId() {
    // Put something in the database so that it is not empty
    ClaimMetricsTestCase testCase = ClaimMetricsTestCase.getInstance();
    testCase.populate(saveToDbService, claimRepository);

    ClaimInfoResponse cir = claimMetricsService.findClaimInfo("not_id");
    assertNull(cir);
  }
}
