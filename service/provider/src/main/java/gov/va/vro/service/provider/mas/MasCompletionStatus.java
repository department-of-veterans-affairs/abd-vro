package gov.va.vro.service.provider.mas;

public enum MasCompletionStatus {
  OFF_RAMP,
  EXAM_ORDER,
  READY_FOR_DECISION;

  public static MasCompletionStatus of(MasCamelStage origin, Boolean sufficientForFastTracking) {
    if (origin == MasCamelStage.START_COMPLETE || sufficientForFastTracking == null) {
      return OFF_RAMP;
    }
    return sufficientForFastTracking ? READY_FOR_DECISION : EXAM_ORDER;
  }
}
