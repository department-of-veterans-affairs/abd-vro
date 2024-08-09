package gov.va.vro.metricslogging;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.stream.Stream;

@Slf4j
public class LocalEnvironmentCondition implements Condition {

  private final String[] LOCAL_ENVS = new String[] {"local", "test"};

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String env = getEnvironment();

    boolean isMatch =
        (StringUtils.isBlank(env))
            || Stream.of(LOCAL_ENVS).anyMatch(e -> StringUtils.equals(e, env));

    if (isMatch) {
      log.info("match for LocalEnvironmentCondition", env);
    }

    return isMatch;
  }

  public String getEnvironment() {
    return System.getenv("ENV");
  }
}
