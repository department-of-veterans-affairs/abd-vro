package gov.va.vro.end2end.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfText {
  private String pdfText;

  public PdfText(String pdfText) {
    this.pdfText = pdfText;
  }

  private int findOccurrence(String target) {
    return (pdfText.length() - pdfText.replace(target, "").length()) / target.length();
  }

  public int countBpReadings() {
    return findOccurrence("Blood pressure:");
  }

  public int countMedications() {
    return findOccurrence("Prescribed on:");
  }

  public boolean hasVeteranInfo(JsonNode veteranInfo) {
    // Assume all exists for now. That is how tst data is set up.
    String first = veteranInfo.get("first").asText();
    String middle = veteranInfo.get("middle").asText();
    String last = veteranInfo.get("last").asText();
    String suffix = veteranInfo.get("suffix").asText();

    String searchValue = String.format("%s %s %s %s", first, middle, last, suffix);

    return pdfText.indexOf(searchValue) > -1;
  }

  public static PdfText getInstance(byte[] pdfContent) throws Exception {
    RandomAccessBuffer buffer = new RandomAccessBuffer(pdfContent);
    PDFParser parser = new PDFParser(buffer);
    parser.parse();

    PDDocument document = parser.getPDDocument();

    PDFTextStripper stripper = new PDFTextStripper();
    stripper.setSortByPosition(true);
    String text = stripper.getText(document);
    return new PdfText(text);
  }
}
