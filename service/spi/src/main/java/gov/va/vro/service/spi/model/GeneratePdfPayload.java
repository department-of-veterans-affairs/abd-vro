package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.event.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class GeneratePdfPayload implements Auditable {
  @NonNull private String claimSubmissionId;

  @NonNull private String diagnosticCode;

  @NonNull private String pdfPackage;

  @JsonProperty("veteranInfo")
  private VeteranInfo veteranInfo;

  @JsonProperty("evidence")
  private AbdEvidence evidence;

  private String status;
  private String reason;

  @Override
  public String getEventId() {
    return claimSubmissionId;
  }
}
