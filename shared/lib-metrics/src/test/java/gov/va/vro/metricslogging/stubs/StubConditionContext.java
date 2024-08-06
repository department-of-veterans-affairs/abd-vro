package gov.va.vro.metricslogging.stubs;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

public class StubConditionContext implements ConditionContext {
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
    return null;
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
