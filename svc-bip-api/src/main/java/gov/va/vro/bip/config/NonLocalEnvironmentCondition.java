package gov.va.vro.bip.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NonLocalEnvironmentCondition implements Condition {

  private final LocalEnvironmentCondition localEnvironmentCondition =
      new LocalEnvironmentCondition();

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return !localEnvironmentCondition.matches(context, metadata);
  }
}
