package gov.va.vro.mockbipce.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

  public String getUrl(String endPoint) {
    return "https://localhost:" + port + endPoint;
  }

  public static TestSpec getBasicExample() {
    return TestSpec.builder()
        .veteranFileNumber("763789990")
        .fileContent("Hello World !!, This is a test file.")
        .fileName("example.pdf")
        .port(8094)
        .build();
  }
}
