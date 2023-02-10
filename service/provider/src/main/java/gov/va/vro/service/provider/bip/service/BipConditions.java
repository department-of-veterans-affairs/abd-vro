package gov.va.vro.service.provider.bip.service;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Set;

public interface BipConditions {

  Set<String> TEST_ENVS = Set.of("local", "end2end-test");

  // End to end test call the mock claims evidence api
  Set<String> TEST_ENVS_CE = Set.of("default", "local", "test");

  class LocalEnvCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      var profiles = context.getEnvironment().getActiveProfiles();
      return Arrays.stream(profiles).anyMatch(TEST_ENVS::contains);
    }
  }

  class HigherEnvCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      var profiles = context.getEnvironment().getActiveProfiles();
      return Arrays.stream(profiles).noneMatch(TEST_ENVS::contains);
    }
  }

  class LocalEnvCeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      var profiles = context.getEnvironment().getActiveProfiles();
      return Arrays.stream(profiles).anyMatch(TEST_ENVS_CE::contains);
    }
  }

  class NonLocalEnvCeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      var profiles = context.getEnvironment().getActiveProfiles();
      return Arrays.stream(profiles).noneMatch(TEST_ENVS_CE::contains);
    }
  }
}
