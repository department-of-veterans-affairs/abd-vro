package gov.va.vro.model.claimmetrics;

import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExamOrdersInfo {
  private List<ExamOrderInfoResponse> examOrderInfoList;
  private long total;

  static void filterConfirmation() {
    if
    System.out.println("Hello World!");
  }
}
