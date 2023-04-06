package gov.va.vro.model.event;

import lombok.Getter;

/** @author warren @Date 4/6/23 */
@Getter
public enum EventReason {
  SUFFICIENCY_UNDETERMINED("assessorError", "Sufficiency cannot be determined."),
  NEW_NOT_PRESUMPTIVE(
      "newClaimMissingFlash266", "New claim cannot be determined to be presumptive."),
  PDF_UPLOAD_FAILED_AFTER_ORDER_EXAM(
      "docUploadFailed", "Failed to upload PDF file after order exam."),
  EXAM_ORDER_FAILED("examOrderFailed", "Failed to order exam."),
  ANNOTATIONS_FAILED("annotationDataRequestFailed", "Failed to get annotation data.");

  private String code;
  private String narrative;

  EventReason(String code, String narrative) {
    this.code = code;
    this.narrative = narrative;
  }

  public static EventReason getEventReason(String reasonCode) {
    for (EventReason reason : EventReason.values()) {
      if (reasonCode.equals(reason.getCode())) {
        return reason;
      }
    }
    return null;
  }
}
