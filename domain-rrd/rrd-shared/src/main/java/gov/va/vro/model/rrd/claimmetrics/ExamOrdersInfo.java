package gov.va.vro.model.rrd.claimmetrics;

import gov.va.vro.model.rrd.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.model.rrd.event.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExamOrdersInfo implements Auditable {
  private List<ExamOrderInfoResponse> examOrderInfoList;
  private long total;
  @Override
  public String getEventId() {
    return "ExamOrderSlackEvent";
  }

  @Override
  public Map<String, String> getDetails() {
    Map<String, String> detailsMap = new HashMap<>();
    for (ExamOrderInfoResponse exam : examOrderInfoList) {
      String examInfo =
              "collectionId: "
                      + exam.getCollectionId()
                      + " createdAt: "
                      + exam.getCreatedAt()
                      + " status: "
                      + exam.getStatus();
      detailsMap.put("ExamOrder", examInfo);
    }
    return detailsMap;
  }

  @Override
  public String getDisplayName() {
    return "ExamOrderSlackEvent";
  }
}
