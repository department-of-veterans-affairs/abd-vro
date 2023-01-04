package gov.va.vro.service.provider.bip.service;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;

public class LocalEnvCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    var profiles = context.getEnvironment().getActiveProfiles();
    return Arrays.stream(profiles).anyMatch(env -> env.equals("test") || env.equals("local"));
  }
}
