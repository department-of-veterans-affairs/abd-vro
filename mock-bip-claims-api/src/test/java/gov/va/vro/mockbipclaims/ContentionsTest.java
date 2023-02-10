package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.mockbipclaims.configuration.TestConfig;
import gov.va.vro.mockbipclaims.mapper.ContentionMapper;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
import gov.va.vro.mockbipclaims.model.ExistingContention;
import gov.va.vro.mockbipclaims.util.TestHelper;
import gov.va.vro.mockbipclaims.util.TestSpec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Import(TestConfig.class)
@ActiveProfiles("test")
public class ContentionsTest {
  @LocalServerPort int port;

  @Autowired private TestHelper helper;

  @Autowired private ContentionMapper mapper;

  @Test
  void claim1010ContentionTest() {
    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);

    boolean updatedBefore = helper.isContentionsUpdated(spec);
    assertEquals(false, updatedBefore);

    List<ContentionSummary> contentions = helper.getContentionSummaries(spec);
    assertEquals(1, contentions.size());
    ContentionSummary summary = contentions.get(0);
    assertEquals(1011, summary.getContentionId());
    var codes = summary.getSpecialIssueCodes();
    assertTrue(codes.indexOf("RRD") >= 0);
    assertTrue(codes.indexOf("RDR1") >= 0);

    summary.getSpecialIssueCodes().remove(0);
    summary.getSpecialIssueCodes().remove(0);
    summary.getSpecialIssueCodes().add("other");
    ExistingContention existingContention = mapper.toExistingContention(summary);
    var responsePut = helper.putContentions(spec, existingContention);
    assertEquals(HttpStatus.OK, responsePut.getStatusCode());

    boolean updated = helper.isContentionsUpdated(spec);
    assertEquals(true, updated);

    List<ContentionSummary> contentionsBack = helper.getContentionSummaries(spec);
    assertEquals(1, contentionsBack.size());
    ContentionSummary summaryBack = contentionsBack.get(0);
    assertEquals(1011, summaryBack.getContentionId());
    var codesBack = summaryBack.getSpecialIssueCodes();
    assertEquals(1, codesBack.size());
    assertEquals("other", codes.get(0));
  }
}
