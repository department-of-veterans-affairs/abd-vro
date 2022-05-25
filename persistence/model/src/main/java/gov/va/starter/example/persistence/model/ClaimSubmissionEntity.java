package gov.va.starter.example.persistence.model;

import gov.va.vro.model.ClaimStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;

@Entity
@Table(name = "claimsubmission", schema = "example")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClaimSubmissionEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(
      name = "uuid",
      strategy = "uuid2",
      parameters = {})
  @XmlAttribute
  private String id;

  @NonNull private final Instant createdAt = Instant.now();

  @NonNull private String userName;
  @NonNull private String pii;
  @NonNull private String firstName;
  @NonNull private String lastName;

  @NonNull private String submissionId;
  @NonNull private String claimantId;

  @NonNull private String contentionType;

  @Enumerated(EnumType.STRING)
  private ClaimStatus status = ClaimStatus.CREATED;
}
