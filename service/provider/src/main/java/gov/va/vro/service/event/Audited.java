package gov.va.vro.service.event;

import gov.va.vro.model.event.EventProcessingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

  EventProcessingType eventType();

  /**
   * Class property that supplies the ID field
   *
   * @return the property name for the id
   */
  String idProperty();

  Class<?> payloadClass();
}
