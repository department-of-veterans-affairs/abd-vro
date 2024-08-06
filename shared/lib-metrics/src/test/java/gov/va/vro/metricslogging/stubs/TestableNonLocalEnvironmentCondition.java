package gov.va.vro.metricslogging.stubs;

import gov.va.vro.metricslogging.LocalEnvironmentCondition;
import gov.va.vro.metricslogging.NonLocalEnvironmentCondition;

public class TestableNonLocalEnvironmentCondition extends NonLocalEnvironmentCondition {

  public TestableNonLocalEnvironmentCondition(String environmentName) {
    localEnvironmentCondition = new TestableLocalEnvironmentCondition(environmentName);
  }
}
