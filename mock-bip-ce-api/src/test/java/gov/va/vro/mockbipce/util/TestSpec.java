package gov.va.vro.mockbipce.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TestSpec {
  private String fileName;
  private String fileContent;
  private String veteranFileNumber;
  private int port;
  private boolean ignoreJwt = false;

  public String getUrl(String endPoint) {
    return "https://localhost:" + port + endPoint;
  }

  /**
   * Generates a basic test specification that can be used in multiple tests.
   *
   * @return TestSpec Test Specification
   */
  public static TestSpec getBasicExample() {
    return TestSpec.builder()
        .veteranFileNumber("763789990")
        .fileContent("Hello World !!, This is a test file.")
        .fileName("example.pdf")
        .port(8094)
        .build();
  }
}
