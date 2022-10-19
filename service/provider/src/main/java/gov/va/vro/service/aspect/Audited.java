package gov.va.vro.service.aspect;

import gov.va.vro.model.event.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

  EventType eventType();

  /**
   * Class property that supplies the ID field
   *
   * @return the property name for the id
   */
  String idProperty();

  Class<?> payloadClass();
}
