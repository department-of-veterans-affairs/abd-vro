package gov.va.vro.metricslogging;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.metricslogging.stubs.StubConditionContext;
import gov.va.vro.metricslogging.stubs.TestableNonLocalEnvironmentCondition;
import org.junit.jupiter.api.Test;

public class NonLocalEnvironmentConditionTest {

  @Test
  void testReturnsFalseForLocalEnv() {
    TestableNonLocalEnvironmentCondition condition =
        new TestableNonLocalEnvironmentCondition("local");
    assertFalse(condition.matches(new StubConditionContext(), null));
  }

  @Test
  void testReturnsFalseForTestEnv() {
    TestableNonLocalEnvironmentCondition condition =
        new TestableNonLocalEnvironmentCondition("test");
    assertFalse(condition.matches(new StubConditionContext(), null));
  }

  @Test
  void testReturnsFalseForBlankEnv() {
    TestableNonLocalEnvironmentCondition condition =
        new TestableNonLocalEnvironmentCondition("test");
    assertFalse(condition.matches(new StubConditionContext(), null));
  }

  @Test
  void testReturnsTrueForPreprodEnvironments() {
    String[] preprodEnvironments = new String[] {"dev", "qa", "sandbox"};

    for (String env : preprodEnvironments) {
      TestableNonLocalEnvironmentCondition condition =
          new TestableNonLocalEnvironmentCondition(env);
      assertTrue(condition.matches(new StubConditionContext(), null));
    }
  }

  @Test
  void testReturnsTrueForProdEnvironments() {
    String[] prodEnvironments = new String[] {"prod-test", "prod", "production"};

    for (String env : prodEnvironments) {
      TestableNonLocalEnvironmentCondition condition =
          new TestableNonLocalEnvironmentCondition(env);
      assertTrue(condition.matches(new StubConditionContext(), null));
    }
  }
}
