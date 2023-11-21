package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/** Data object used in create claim contention(s) endpoint: POST /claims/{claimId}/contentions */
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Contention {
  @JsonProperty("medicalInd")
  private boolean medicalInd;

  @JsonProperty("beginDate")
  private String beginDate;

  @JsonProperty("createDate")
  private String createDate;

  @JsonProperty("altContentionName")
  private String altContentionName;

  @JsonProperty("completedDate")
  private String completedDate;

  @JsonProperty("notificationDate")
  private String notificationDate;

  @JsonProperty("contentionTypeCode")
  private String contentionTypeCode;

  @JsonProperty("classificationType")
  private int classificationType;

  @JsonProperty("diagnosticTypeCode")
  private int diagnosticTypeCode;

  @JsonProperty("claimantText")
  private String claimantText;

  @JsonProperty("contentionStatusTypeCode")
  private String contentionStatusTypeCode;

  @JsonProperty("originalSourceTypeCode")
  private String originalSourceTypeCode;

  @JsonProperty("specialIssueCodes")
  private List<String> specialIssueCodes;

  @JsonProperty("associatedTrackedItems")
  private List<TrackedItemAssociations> associatedTrackedItems;
}
