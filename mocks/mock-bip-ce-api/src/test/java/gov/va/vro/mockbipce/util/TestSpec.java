package gov.va.vro.mockbipce.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestSpec {
  private String fileName;
  private String fileContent;
  private String veteranFileNumber;
  private int port;
  private boolean ignoreJwt = false;
  private boolean ignoreFolderUri = false;
  private String idType = "FILENUMBER";

  public String getUrl(String endPoint) {
    return "https://localhost:" + port + endPoint;
  }

  public String getReceivedFilesUrl() {
    String baseUrl = getUrl("/received-files/");
    return baseUrl + veteranFileNumber;
  }

  /**
   * Generates a basic test specification that can be used in multiple tests.
   *
   * @return TestSpec Test Specification
   */
  public static TestSpec getBasicExample() {
    TestSpec spec = new TestSpec();
    spec.setVeteranFileNumber("763789990");
    spec.setFileContent("Hello World !!, This is a test file.");
    spec.setFileName("example.pdf");
    spec.setPort(20310);
    return spec;
  }
}
