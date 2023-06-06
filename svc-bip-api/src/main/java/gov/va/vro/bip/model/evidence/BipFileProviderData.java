package gov.va.vro.bip.model.evidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The data object is used in BIP Evidence File Upload API.
 *
 * @author warren @Date 11/11/22
 */
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BipFileProviderData {
  @Schema(description = "Content Source", example = "VRO")
  private String contentSource;

  @Schema(description = "Claimant First Name", example = "John")
  private String claimantFirstName;

  @Schema(description = "Claimant Middle Initial", example = "M")
  private String claimantMiddleInitial;

  @Schema(description = "Claimant Last Name", example = "Smith")
  private String claimantLastName;

  @Schema(description = "Social Security Number", example = "204972342")
  private String claimantSsn;

  @Schema(description = "Benefit Type ID Number")
  private Integer benefitTypeId;

  @Schema(description = "Document Type ID", example = "")
  private int documentTypeId;

  @Schema(description = "Date VA Received Document", example = "2022-02-01")
  private String dateVaReceivedDocument;

  @Schema(description = "Subject", example = "subject")
  private String subject;

  @Schema(description = "Contention list")
  private List<String> contentions;

  @Schema(description = "Alternative document type IDs")
  private List<Integer> alternativeDocumentTypeIds;

  @Schema(description = "Actionable flag")
  private boolean actionable;

  @Schema(description = "A list of associate claim IDs")
  private List<String> associatedClaimIds;

  @Schema(description = "Document Notes", example = "Joe Doe had previous exam.")
  private String notes;

  @Schema(description = "Payee Code")
  private String payeeCode;

  @Schema(description = "End Product Code")
  private String endProductCode;

  @Schema(description = "Regional Processing Office")
  private String regionalProcessingOffice;

  @Schema(description = "facility Code")
  private String facilityCode;

  @Schema(description = "claimant ParticipantId", example = "9320497233")
  private String claimantParticipantId;

  @Schema(description = "Source comment")
  private String sourceComment; // "source comment",

  @Schema(description = "Claimant Date of Birth")
  private String claimantDateOfBirth;
}
