package gov.va.vro.end2end.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * This class represents the text of the evidence pdf as extracted by Apache PDFBox. It is used for
 * sanity checks for now until a more sophisticated parser extracts all data.
 */
@Getter
public class PdfText {
  private String pdfText;

  public PdfText(String pdfText) {
    this.pdfText = pdfText;
  }

  public int countBpReadings() {
    return StringUtils.countMatches(pdfText, "Blood pressure:");
  }

  public int countMedications() {
    return StringUtils.countMatches(pdfText, "Prescribed on:");
  }

  /**
   * Checks if the veteran full name is the document.
   *
   * @param veteranInfo veteran demographics spec
   * @return if the pdf text has veteran full name
   */
  public boolean hasVeteranInfo(JsonNode veteranInfo) {
    // Assume all exists for now. That is how test data is set up.
    String first = veteranInfo.get("first").asText();
    String middle = veteranInfo.get("middle").asText();
    String last = veteranInfo.get("last").asText();
    String suffix = veteranInfo.get("suffix").asText();

    String searchValue = String.format("%s %s %s %s", first, middle, last, suffix);

    return pdfText.indexOf(searchValue) > -1;
  }

  /**
   * Extracts text from the pdf file and caches for later use.
   *
   * @param pdfContent the evidence pdf file as byte array
   * @return newed PDFText
   * @throws Exception any error to fail the test
   */
  public static PdfText getInstance(byte[] pdfContent) throws Exception {
    String text = PdfUtil.getText(pdfContent);
    return new PdfText(text);
  }
}
