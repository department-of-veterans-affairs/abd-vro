package gov.va.vro.model.biekafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define field tha is exclusive to certain contention
 * classification events. Valid values are from ContentionEvent enum topic names
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) // This ensures the annotation is only used on fields
public @interface TargetEvents {
  String[] value(); // An array of strings as the annotation's value
}
