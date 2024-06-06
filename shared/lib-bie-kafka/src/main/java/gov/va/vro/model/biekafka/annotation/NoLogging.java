package gov.va.vro.model.biekafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark fields that contain sensitive or confidential information
 * that should be excluded from logging to protect privacy or security.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoLogging {
  // No value needed
}
