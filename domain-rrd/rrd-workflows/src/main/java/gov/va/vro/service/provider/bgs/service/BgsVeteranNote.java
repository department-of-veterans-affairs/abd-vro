package gov.va.vro.service.provider.bgs.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

class BgsVeteranNote {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  // The date should be in MM/DD/YYYY format
  static final String ARSD_UPLOADED_NOTE =
      "Hypertension automated review summary document uploaded %s";

  static String getArsdUploadedNote(OffsetDateTime docUploadedAt) {
    String dateString = docUploadedAt.format(formatter);
    return String.format(ARSD_UPLOADED_NOTE, dateString);
  }
}
