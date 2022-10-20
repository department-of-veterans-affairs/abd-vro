package gov.va.vro.service.event;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.EventProcessingType;
import gov.va.vro.model.event.EventType;
import jodd.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventAspect {

  private final EventService eventService;

  @Around(value = "@annotation(gov.va.vro.service.event.Audited)")
  @SneakyThrows
  public Object logAuditEvent(ProceedingJoinPoint joinPoint) {
    Audited annotation = getAuditedAnnotation(joinPoint);
    var eventProcessingType = annotation.eventType();
    var payLoadClass = annotation.payloadClass();
    var idProperty = annotation.idProperty();

    String eventId = resolveId(joinPoint, payLoadClass, idProperty);
    if (eventId == null) {
      log.warn("Cannot extract event id. No message will be logged.");
      return joinPoint.proceed();
    }

    AuditEvent startEvent =
        getAuditEvent(eventProcessingType, payLoadClass, eventId, EventType.ENTERING);
    eventService.logEvent(startEvent);
    log.debug("Entering " + joinPoint.getSignature());
    try {
      Object value = joinPoint.proceed();
      log.debug("Exiting " + joinPoint.getSignature());
      var endEvent = getAuditEvent(eventProcessingType, payLoadClass, eventId, EventType.EXITING);
      eventService.logEvent(endEvent);
      return value;
    } catch (Throwable t) {
      log.debug("Exception " + t.getMessage());
      eventService.logEvent(
          getExceptionEvent(EventProcessingType.AUTOMATED_CLAIM, payLoadClass, eventId, t));
      throw t;
    }
  }

  private static AuditEvent getExceptionEvent(
      EventProcessingType eventProcessingType, Class<?> payLoadClass, String eventId, Throwable t) {
    return AuditEvent.builder()
        .eventId(eventId)
        .eventType(EventType.EXCEPTION)
        .processingType(eventProcessingType)
        .payloadType(payLoadClass)
        .exception(t)
        .build();
  }

  private static AuditEvent getAuditEvent(
      EventProcessingType eventProcessingType,
      Class<?> payLoadClass,
      String eventId,
      EventType eventType) {
    return AuditEvent.builder()
        .eventId(eventId)
        .eventType(eventType)
        .processingType(eventProcessingType)
        .payloadType(payLoadClass)
        .build();
  }

  private String resolveId(
      ProceedingJoinPoint joinPoint, Class<?> payloadClass, String propertyId) {
    for (Object arg : joinPoint.getArgs()) {
      if (payloadClass.isAssignableFrom(arg.getClass())) {
        Object value = BeanUtil.declaredForcedSilent.getProperty(arg, propertyId);
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }
    return null;
  }

  private Audited getAuditedAnnotation(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return method.getAnnotation(Audited.class);
  }
}
