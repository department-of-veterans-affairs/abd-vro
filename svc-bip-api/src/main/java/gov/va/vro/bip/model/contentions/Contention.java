package gov.va.vro.bip.model.contentions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.List;

/** Data object used in create claim contention(s) endpoint: POST /claims/{claimId}/contentions */
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public class Contention {
  @JsonProperty("medicalInd")
  private boolean medicalInd;

  @JsonProperty("beginDate")
  private OffsetDateTime beginDate;

  @JsonProperty("createDate")
  private OffsetDateTime createDate;

  @JsonProperty("completedDate")
  private OffsetDateTime completedDate;

  @JsonProperty("altContentionName")
  private String altContentionName;

  @JsonProperty("notificationDate")
  private OffsetDateTime notificationDate;

  @JsonProperty("contentionTypeCode")
  private String contentionTypeCode;

  @JsonProperty("classificationType")
  private int classificationType;

  @JsonProperty("diagnosticTypeCode")
  private String diagnosticTypeCode;

  @JsonProperty("claimantText")
  private String claimantText;

  @JsonProperty("contentionStatusTypeCode")
  private String contentionStatusTypeCode;

  @JsonProperty("originalSourceTypeCode")
  private String originalSourceTypeCode;

  @JsonProperty("specialIssueCodes")
  private List<String> specialIssueCodes;

  @JsonProperty("associatedTrackedItems")
  private List<TrackedItemAssociation> associatedTrackedItems;
}
