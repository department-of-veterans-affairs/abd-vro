package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.ServiceLocation;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.mas.ClaimCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class GeneratePdfPayload {
  @NonNull private String claimSubmissionId;

  @NonNull private String diagnosticCode;

  @JsonProperty("veteranInfo")
  private VeteranInfo veteranInfo;

  @JsonProperty("serviceLocations")
  private List<ServiceLocation> serviceLocations;

  @NotNull private ClaimCondition conditions;

  @JsonProperty("evidence")
  private AbdEvidence evidence;

  private String status;
  private String reason;

  private String pdfTemplate;
  private String pdfLibrary;

  private String veteranFileId;

  @JsonIgnore
  public static String createPdfFilename(String diagnosis) {
    String timestamp = String.format("%1$tY%1$tm%1$td", new Date());
    return String.format("VAMC_%s_Rapid_Decision_Evidence--%s.pdf", diagnosis, timestamp);
  }
}
