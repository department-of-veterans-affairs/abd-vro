package gov.va.starter.example.claimsubmission.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClaimSubmissionData {

  private String id;

  @NonNull private String userName;
  @NonNull private String pii;
  @NonNull private String firstName;
  @NonNull private String lastName;
  private String fullName;

  @NonNull private String submissionId;
  @NonNull private String claimantId;

  @NonNull private String contentionType;
}
