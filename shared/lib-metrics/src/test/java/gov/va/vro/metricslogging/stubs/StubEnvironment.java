package gov.va.vro.metricslogging.stubs;

import org.springframework.core.env.StandardEnvironment;

public class StubEnvironment extends StandardEnvironment {

    private String environmentName;

    public StubEnvironment(String environmentName) {
        this.environmentName = environmentName;
    }

    @Override
    public String getProperty(String key) {
        return environmentName;
    }
}
