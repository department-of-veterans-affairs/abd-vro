package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class BipUpdateClaimResult {
  private final boolean success;

  public BipUpdateClaimResult(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  private String message;
  private Throwable throwable;

  public boolean hasSlackEvent() {
    return message != null || throwable != null;
  }

  public AuditEvent toAuditEvent(String routeId, MasProcessingObject masProcessingObject) {
    if (throwable != null) {
      return AuditEvent.fromException(masProcessingObject, routeId, throwable);
    } else {
      return AuditEvent.fromAuditable(masProcessingObject, routeId, message);
    }
  }

  public static BipUpdateClaimResult ofError(String message) {
    log.error(message);
    return new BipUpdateClaimResult(false, message);
  }

  public static BipUpdateClaimResult ofWarning(String message) {
    log.warn(message);
    return new BipUpdateClaimResult(true, message);
  }

  public static BipUpdateClaimResult ofThrowable(String message, Throwable throwable) {
    log.error(message);
    return new BipUpdateClaimResult(false, message, throwable);
  }
}
