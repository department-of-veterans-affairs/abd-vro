package gov.va.vro.service.provider.bgs.service;

import gov.va.vro.model.rrd.event.EventReason;

import java.util.Map;

class BgsClaimNotes {

  static final String RFD_NOTE = "Claim status updated to RFD via automation";

  static final String ARSD_COMPLETED_NOTE =
      "Please note, the automated review summary document (ARSD) has been completed in full. "
          + "One or more contentions were determined to be RFD by current rating criteria. "
          + "Necessary exams were requested or drafted.";

  static final String EXAM_REQUESTED_NOTE =
      "Please note, the automated review summary document has been completed in full with the "
          + "necessary exam(s) requested or drafted based on automation eligible contention(s). "
          + "This claim/contention has been placed in the Open Status Development Life Cycle for "
          + "further review by a claims processor.";

  static final String CANT_CONFIRM_PRESUMPTIVE_NOTE =
      "Automation-eligible contentions reviewed, but no action taken based on existing evidence. "
          + "This claim has been removed from the automation program and placed in Open status for "
          + "development by a claims processor.";
  static final String ARSD_NOT_UPLOADED_NOTE =
      "Claim was offramped from VBA automation platform when ARSD could not be uploaded to eFolder.";

  static final Map<String, String> OFFRAMP_ERROR_2_CLAIM_NOTE =
      Map.of(
          // aka newClaimMissingFlash266
          EventReason.NEW_NOT_PRESUMPTIVE.getCode(),
          CANT_CONFIRM_PRESUMPTIVE_NOTE,
          // aka insufficientHealthDataToOrderExam
          EventReason.SUFFICIENCY_UNDETERMINED.getCode(),
          CANT_CONFIRM_PRESUMPTIVE_NOTE,
          // aka docUploadFailed
          EventReason.PDF_UPLOAD_FAILED_AFTER_ORDER_EXAM.getCode(),
          ARSD_NOT_UPLOADED_NOTE);
}
