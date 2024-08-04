package gov.va.vro.metricslogging;

import gov.va.vro.metricslogging.stubs.StubConditionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NonLocalEnvironmentConditionTest {

    @Test
    void testReturnsFalseForLocalEnv() {
        NonLocalEnvironmentCondition condition = new NonLocalEnvironmentCondition();
        assertFalse(condition.matches(new StubConditionContext("local"), null));
    }

    @Test
    void testReturnsFalseForTestEnv() {
        NonLocalEnvironmentCondition condition = new NonLocalEnvironmentCondition();
        assertFalse(condition.matches(new StubConditionContext("test"), null));
    }

    @Test
    void testReturnsTrueForPreprodNamespaces() {
        NonLocalEnvironmentCondition condition = new NonLocalEnvironmentCondition();
        assertTrue(condition.matches(new StubConditionContext("dev"), null));
        assertTrue(condition.matches(new StubConditionContext("qa"), null));
        assertTrue(condition.matches(new StubConditionContext("sandbox"), null));
    }

    @Test
    void testReturnsTrueForProdNamespaces() {
        NonLocalEnvironmentCondition condition = new NonLocalEnvironmentCondition();
        assertTrue(condition.matches(new StubConditionContext("prod-test"), null));
        assertTrue(condition.matches(new StubConditionContext("prod"), null));
        assertTrue(condition.matches(new StubConditionContext("production"), null));
    }
}
