package gov.va.vro.end2end.util;

import lombok.Getter;

/**
 * This class represents the text of the evidence pdf as extracted by Apache PDFBox. It is used for
 * sanity checks for now until a more sophisticated parser extracts all data.
 */
@Getter
public class PdfTextV2 {
  private String pdfText;

  public PdfTextV2(String pdfText) {
    this.pdfText = pdfText;
  }

  /**
   * Checks if the veteran name is in the document.
   *
   * @param firstName first name of the Veteran
   * @param lastName last name of the Veteran
   * @return if the pdf text has the Veteran full name
   */
  public boolean hasVeteranName(String firstName, String lastName) {
    String fullName = firstName + " " + lastName;
    return pdfText.indexOf(fullName) > -1;
  }

  /**
   * Extracts text from the pdf file and caches for later use.
   *
   * @param pdfContent the evidence pdf file as byte array
   * @return newed PDFTextV2
   * @throws Exception any error to fail the test
   */
  public static PdfTextV2 getInstance(byte[] pdfContent) throws Exception {
    String text = PdfUtil.getText(pdfContent);
    return new PdfTextV2(text);
  }
}
