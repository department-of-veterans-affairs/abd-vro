package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A condition or diagnosis that a Veteran contends are the cause of a current disability, and may
 * qualify for benefits, if directly related to a Veteran&#39;s military service.
 */
@Schema(
    name = "Contention",
    description =
        """
        A condition or diagnosis that a Veteran contends are the cause of a current disability,
        and may qualify for benefits, if directly related to a Veteran's military service.
        """)
@Data
public class Contention {
  @NotNull private Boolean medicalInd;

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
}
