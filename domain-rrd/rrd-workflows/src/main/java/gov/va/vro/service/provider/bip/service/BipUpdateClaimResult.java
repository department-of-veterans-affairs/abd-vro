package gov.va.vro.service.provider.bip.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class BipUpdateClaimResult {
  @NonNull private final boolean success;
  private String message;

  public boolean hasMessage() {
    return message != null;
  }

  public static BipUpdateClaimResult ofError(String message) {
    log.error(message);
    return new BipUpdateClaimResult(false, message);
  }

  public static BipUpdateClaimResult ofWarning(String message) {
    log.warn(message);
    return new BipUpdateClaimResult(true, message);
  }
}
