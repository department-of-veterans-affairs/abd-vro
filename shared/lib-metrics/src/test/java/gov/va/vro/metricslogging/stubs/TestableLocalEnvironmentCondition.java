package gov.va.vro.metricslogging.stubs;

import gov.va.vro.metricslogging.LocalEnvironmentCondition;

public class TestableLocalEnvironmentCondition extends LocalEnvironmentCondition {

  private final String environmentName;

  public TestableLocalEnvironmentCondition(String environmentName) {
    this.environmentName = environmentName;
  }

  @Override
  public String getEnvironment() {
    return environmentName;
  }
}
