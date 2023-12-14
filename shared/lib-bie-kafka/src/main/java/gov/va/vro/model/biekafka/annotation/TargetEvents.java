package gov.va.vro.model.biekafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: this annotation can be used for the new description field that only exists in
 * ContentionUpdated and ContentionDeleted kafka events This annotation is used to define field that
 * is exclusive to certain contention classification events,
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) // This ensures the annotation is only used on fields
public @interface TargetEvents {
  String[] value(); // An array of strings as the annotation's value
}
