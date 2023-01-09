package gov.va.vro.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A data object is used in a claim contention creation.
 *
 * @author warren @Date 11/14/22
 */
@Getter
@Setter
public class CreateContention {
  private boolean medicalInd;

  @Schema(description = "Date begun", example = "2022-12-06T20:19:57.350Z")
  private String beginDate;

  @Schema(description = "Date created", example = "2022-12-06T20:12:57.350Z")
  private String createDate;

  private String altContentionName;

  @Schema(description = "Date completed", example = "2022-12-07T20:19:57.350Z")
  private String completedDate;

  @Schema(description = "Date begin", example = "2022-12-07T20:19:57.350Z")
  private String notificationDate;

  @Schema(description = "Contention Type Code", example = "NEW")
  private String contentionTypeCode;

  @Schema(description = "classification Type", example = "1250")
  private int classificationType;

  @Schema(description = "diagnostic Type", example = "6100")
  private int diagnosticTypeCode;

  private String claimantText;

  @Schema(description = "contention Status Type Code", example = "C")
  private String contentionStatusTypeCode;

  @Schema(description = "Original Source  Type Code", example = "PHYS")
  private String originalSourceTypeCode;

  @Schema(description = "A list of special Issue Codes", example = "[ \"AOOV\", \"ELIGIBILITY\"]")
  private List<String> specialIssueCodes;

  private List<TrackedItems> associatedTrackedItems;
}
