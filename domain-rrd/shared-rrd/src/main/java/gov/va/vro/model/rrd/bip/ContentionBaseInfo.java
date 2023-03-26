package gov.va.vro.model.rrd.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Contention base information.
 *
 * @author warren @Date 12/9/22
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentionBaseInfo {
  @Schema(description = "Medical Indicator", example = "true")
  protected boolean medicalInd;

  @Schema(description = "Date begun", example = "2022-12-06T20:19:57.350Z")
  protected String beginDate;

  @Schema(description = "Date created", example = "2022-12-06T20:12:57.350Z")
  protected String createDate;

  protected String altContentionName;

  @Schema(description = "Date completed", example = "2022-12-06T20:12:57.350Z")
  protected String completedDate;

  @Schema(description = "Date notified", example = "2022-12-06T20:12:57.350Z")
  protected String notificationDate;

  @Schema(description = "Contention Type Code", example = "NEW")
  protected String contentionTypeCode;

  @Schema(description = "classification Type", example = "1250")
  private int classificationType;

  @Schema(description = "diagnostic Type", example = "6100")
  private String diagnosticTypeCode;

  private String claimantText;

  @Schema(description = "contention Status Type Code", example = "C")
  private String contentionStatusTypeCode;

  @Schema(description = "Original Source  Type Code", example = "PHYS")
  private String originalSourceTypeCode;

  @Schema(description = "A list of special Issue Codes", example = "[ \"AOOV\", \"ELIGIBILITY\"]")
  protected List<String> specialIssueCodes;

  private long contentionId;

  @Schema(description = "Date last modified", example = "2022-12-07T20:19:57.350Z")
  private String lastModified;

  @Schema(description = "Lifecycle status", example = "Ready for Decision")
  private String lifecycleStatus;

  @Schema(description = "Automation Indicator", example = "false")
  private boolean automationIndicator;
}
