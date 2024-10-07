package gov.va.vro.metricslogging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
public class NonLocalEnvironmentCondition implements Condition {

  protected LocalEnvironmentCondition localEnvironmentCondition = new LocalEnvironmentCondition();

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    boolean isMatch = !localEnvironmentCondition.matches(context, metadata);

    if (isMatch) {
      log.debug("match for NonLocalEnvironmentCondition");
    }

    return isMatch;
  }
}
