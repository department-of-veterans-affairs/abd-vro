package gov.va.starter.example.service.spi.claimsubmission.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
// https://stackoverflow.com/questions/58171839/using-lombok-requiredargsconstructor-as-jsoncreator
@RequiredArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@AllArgsConstructor(onConstructor_ = {@JsonIgnore})
@EqualsAndHashCode
@Builder
@ToString(includeFieldNames = true)
// @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClaimSubmission {

  private String id;
  @NonNull private final Instant createdAt = Instant.now();

  @NonNull private String userName;
  @NonNull private String pii;
  @NonNull private String firstName;
  @NonNull private String lastName;

  @NonNull private String submissionId;
  @NonNull private String claimantId;

  @NonNull private String contentionType;

  @Builder.Default private ClaimStatus status = ClaimStatus.CREATED;
}
