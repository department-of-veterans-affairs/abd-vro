package gov.va.vro.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class Claim implements Serializable {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "uuid", updatable = false, nullable = false)
  private String uuid;

  private final Date createdAt = new Date();

  private String submission_id;
  private String claimant_id;

  private String claim_id;
  private String contention_type;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ClaimStatus status = ClaimStatus.CREATED;

  //    private List<Payload> payloads;
}
