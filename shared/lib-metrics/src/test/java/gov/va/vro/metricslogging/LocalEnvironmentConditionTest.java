package gov.va.vro.metricslogging;

import gov.va.vro.metricslogging.stubs.StubConditionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalEnvironmentConditionTest {

    @Test
    void testReturnsTrueForLocalEnv() {
        LocalEnvironmentCondition condition = new LocalEnvironmentCondition();
        assertTrue(condition.matches(new StubConditionContext("local"), null));
    }

    @Test
    void testReturnsTrueForTestEnv() {
        LocalEnvironmentCondition condition = new LocalEnvironmentCondition();
        assertTrue(condition.matches(new StubConditionContext("test"), null));
    }

    @Test
    void testReturnsFalseForBlankEnv() {
        LocalEnvironmentCondition condition = new LocalEnvironmentCondition();
        assertFalse(condition.matches(new StubConditionContext(""), null));
    }

    @Test
    void testReturnsFalseForPreprodNamespaces() {
        LocalEnvironmentCondition condition = new LocalEnvironmentCondition();
        assertFalse(condition.matches(new StubConditionContext("dev"), null));
        assertFalse(condition.matches(new StubConditionContext("qa"), null));
        assertFalse(condition.matches(new StubConditionContext("sandbox"), null));
    }

    @Test
    void testReturnsFalseForProdNamespaces() {
        LocalEnvironmentCondition condition = new LocalEnvironmentCondition();
        assertFalse(condition.matches(new StubConditionContext("prod-test"), null));
        assertFalse(condition.matches(new StubConditionContext("prod"), null));
        assertFalse(condition.matches(new StubConditionContext("production"), null));
    }
}
