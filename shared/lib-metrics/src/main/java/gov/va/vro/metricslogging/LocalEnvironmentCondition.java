package gov.va.vro.metricslogging;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.stream.Stream;

public class LocalEnvironmentCondition implements Condition {

  private final String[] LOCAL_ENVS = new String[] {"local", "test"};

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String env = getEnvironment();

    if (StringUtils.isBlank(env)) {
      return true;
    }

    return Stream.of(LOCAL_ENVS).anyMatch(e -> StringUtils.equals(e, env));
  }

  public String getEnvironment() {
    return System.getenv("ENV");
  }
}
