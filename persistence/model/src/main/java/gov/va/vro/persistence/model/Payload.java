package gov.va.vro.persistence.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class Payload implements Serializable {
  private final Date createdAt = new Date();
  @NonNull private String submissionId;
  @NonNull private String resultStatus;
  private Map<String, Object> results;
}
