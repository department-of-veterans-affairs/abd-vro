package gov.va.vro.persistence.v2;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "claim_audit_history")
public class ClaimHistoryEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    private String eventId;
    private String routeId;
    private String throwable;
    private String message;
    @NotNull
    private ZonedDateTime eventTime = ZonedDateTime.now();

    @ManyToOne
    private ContentionEntity contention;
    @ManyToOne
    AssessmentResultEntity assessmentResult;

    @ManyToOne
    EvidenceSummaryDocumentEntity evidenceSummaryDocument;
}
