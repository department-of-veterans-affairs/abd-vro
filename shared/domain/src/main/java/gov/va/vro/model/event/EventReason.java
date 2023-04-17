package gov.va.vro.model.event;

import lombok.Getter;

/**
 * The object lists events that can happen in VRO process. Each event has a code and a narrative
 * message. It can be used to handle the event message consistently in the VRO process.
 *
 * @author warren @Date 4/6/23
 */
@Getter
public enum EventReason {
  SUFFICIENCY_UNDETERMINED("assessorError", "Sufficiency cannot be determined."),
  NEW_NOT_PRESUMPTIVE(
      "newClaimMissingFlash266", "New claim cannot be determined to be presumptive."),
  PDF_UPLOAD_FAILED_AFTER_ORDER_EXAM(
      "docUploadFailed", "PDF upload failed after exam order requested."),
  PDF_UPLOAD_FAILED_AFTER_RFD("docUploadFailedRfd", "Failed to upload PDF file."),
  EXAM_ORDER_FAILED("examOrderFailed", "Failed to order exam."),
  ANNOTATIONS_FAILED("annotationDataRequestFailed", "Failed to get annotation data."),
  BIP_UPDATE_FAILED("bipUpdateFailed", "BIP update failed.");

  private static int MAX_CODE_LENGTH = 50;

  private String code;
  private String narrative;

  EventReason(String code, String narrative) {
    this.code = code;
    this.narrative = narrative;
  }

  public String getReasonMessage() {
    return String.format("reason code: %s,  narrative:%s", code, narrative);
  }

  public static EventReason getEventReason(String reasonCode) {
    if (reasonCode.length() < MAX_CODE_LENGTH) {
      for (EventReason reason : EventReason.values()) {
        if (reasonCode.equals(reason.getCode())) {
          return reason;
        }
      }
    }
    return null;
  }
}
