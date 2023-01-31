package gov.va.vro.mockbipce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
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
}
