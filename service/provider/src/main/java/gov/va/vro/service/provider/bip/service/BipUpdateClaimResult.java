package gov.va.vro.service.provider.bip.service;

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
