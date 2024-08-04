package gov.va.vro.metricslogging.stubs;

import gov.va.vro.metricslogging.LocalEnvironmentConditionTest;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

public class StubConditionContext implements ConditionContext {
    private final Environment environment;

    public StubConditionContext(String environmentName) {
        environment = new StubEnvironment(environmentName);
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return null;
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
