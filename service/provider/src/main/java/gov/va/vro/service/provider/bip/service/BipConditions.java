package gov.va.vro.service.provider.bip.service;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Set;

public interface BipConditions {
  // End-to-end tests call the mock claims evidence api
  final Set<String> LOCAL_ENVIRONMENTS = Set.of("default", "local");

  class LocalEnvironmentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      var profiles = context.getEnvironment().getActiveProfiles();
      return Arrays.stream(profiles).anyMatch(LOCAL_ENVIRONMENTS::contains);
    }
  }

  class NonLocalEnvironmentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      var profiles = context.getEnvironment().getActiveProfiles();
      return Arrays.stream(profiles).noneMatch(LOCAL_ENVIRONMENTS::contains);
    }
  }
}
