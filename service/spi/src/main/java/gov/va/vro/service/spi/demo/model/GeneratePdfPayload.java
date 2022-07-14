package gov.va.vro.service.spi.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Deprecated // demo data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString(includeFieldNames = true)
public class GeneratePdfPayload {
  @NonNull private String contention;

  // input JSON string
  @JsonProperty("patient_info")
  private String patientInfo;

  // input JSON string
  @JsonProperty("assessed_data")
  private String assessedData;

  // output JSON string
  private String pdfDocumentJson;
}
