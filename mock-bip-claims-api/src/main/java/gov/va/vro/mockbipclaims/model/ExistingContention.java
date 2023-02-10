package gov.va.vro.mockbipclaims.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.Valid;

/** ExistingContention. */
@Data
public class ExistingContention {
  private Boolean medicalInd;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime beginDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createDate;

  private String altContentionName;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime notificationDate;

  private String contentionTypeCode;

  private Long classificationType;

  private String diagnosticTypeCode;

  private String claimantText;

  private String contentionStatusTypeCode;

  private String originalSourceTypeCode;

  @Valid private List<String> specialIssueCodes = null;

  @Valid private List<TrackedItemAssociationsOnContention> associatedTrackedItems = null;

  private Long contentionId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastModified;

  private String lifecycleStatus;

  private String action;

  private Boolean automationIndicator;
}
