package gov.va.vro.end2end.util;

import lombok.SneakyThrows;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfUtil {
  /**
   * Extracts text from the pdf file.
   *
   * @param pdfContent the evidence pdf file as byte array
   * @return newed PDFText
   * @throws Exception any error to fail the test
   */
  @SneakyThrows
  public static String getText(byte[] pdfContent) {
    RandomAccessBuffer buffer = new RandomAccessBuffer(pdfContent);
    PDFParser parser = new PDFParser(buffer);
    parser.parse();

    PDDocument document = parser.getPDDocument();

    PDFTextStripper stripper = new PDFTextStripper();
    stripper.setSortByPosition(true);
    return stripper.getText(document);
  }
}
