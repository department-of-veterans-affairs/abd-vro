package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

/** An object to provide more detailed data for a specific contention. */
@Schema(
    name = "ContentionDetail",
    description = "An object to provide more detailed data for a specific contention.")
@Data
public class ContentionDetail {

  @NotNull private Boolean medicalInd;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime beginDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createDate;

  private String altContentionName;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime notificationDate;

  @NotNull private String contentionTypeCode;

  @NotNull private Long classificationType;

  private String diagnosticTypeCode;

  @NotNull private String claimantText;

  private String contentionStatusTypeCode;

  private String originalSourceTypeCode;

  @Valid private List<String> specialIssueCodes = null;

  @Valid private List<TrackedItemAssociationsOnContention> associatedTrackedItems = null;

  @NotNull private Long contentionId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastModified;

  private String lifecycleStatus;

  private String action;

  private Boolean automationIndicator;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime summaryDateTime;

  @Valid private List<ContentionHistory> contentionHistory = null;
}
