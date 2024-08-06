package gov.va.vro.metricslogging;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.metricslogging.stubs.StubConditionContext;
import gov.va.vro.metricslogging.stubs.TestableLocalEnvironmentCondition;
import org.junit.jupiter.api.Test;

public class LocalEnvironmentConditionTest {

  @Test
  void testReturnsTrueForLocalEnv() {
    TestableLocalEnvironmentCondition condition = new TestableLocalEnvironmentCondition("local");
    assertTrue(condition.matches(new StubConditionContext(), null));
  }

  @Test
  void testReturnsTrueForTestEnv() {
    TestableLocalEnvironmentCondition condition = new TestableLocalEnvironmentCondition("test");
    assertTrue(condition.matches(new StubConditionContext(), null));
  }

  @Test
  void testReturnsTrueForBlankEnv() {
    TestableLocalEnvironmentCondition condition = new TestableLocalEnvironmentCondition("");
    assertTrue(condition.matches(new StubConditionContext(), null));
  }

  @Test
  void testReturnsFalseForPreprodEnvironments() {
    String[] preprodEnvironments = new String[] {"dev", "qa", "sandbox"};

    for (String env : preprodEnvironments) {
      TestableLocalEnvironmentCondition condition = new TestableLocalEnvironmentCondition(env);
      assertFalse(condition.matches(new StubConditionContext(), null));
    }
  }

  @Test
  void testReturnsFalseForProdEnvironments() {
    String[] prodEnvironments = new String[] {"prod-test", "prod", "production"};

    for (String env : prodEnvironments) {
      TestableLocalEnvironmentCondition condition = new TestableLocalEnvironmentCondition(env);
      assertFalse(condition.matches(new StubConditionContext(), null));
    }
  }
}
