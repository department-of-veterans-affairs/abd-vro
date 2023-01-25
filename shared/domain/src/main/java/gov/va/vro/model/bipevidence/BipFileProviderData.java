package gov.va.vro.model.bipevidence;

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
public class BipFileProviderData {
  @Schema(description = "Content Source", example = "VBMS")
  private String contentSource;

  @Schema(description = "Claimant First Name", example = "John")
  private String claimantFirstName;

  @Schema(description = "Claimant Middle Initial", example = "M")
  private String claimantMiddleInitial;

  @Schema(description = "Claimant Last Name", example = "Smith")
  private String claimantLastName;

  @Schema(description = "Social Security Number", example = "204972342")
  private String claimantSsn;

  @Schema(description = "Benefit Type ID Number", example = "10")
  private int benefitTypeId;

  @Schema(description = "Document Type ID", example = "131")
  private int documentTypeId;

  @Schema(description = "Date VA Received Document", example = "2022-02-01")
  private String dateVaReceivedDocument;

  @Schema(description = "Subject", example = "subject")
  private String subject;

  @Schema(description = "Contention list", example = "[ \"contention 1\" ]")
  private List<String> contentions;

  @Schema(description = "Alternative document type IDs", example = "[ 1, 2]")
  private List<Integer> alternativeDocumentTypeIds;

  @Schema(description = "Social Security Number", example = "204972342")
  private boolean actionable;

  @Schema(description = "A list of associate claim IDs", example = "[ \"139278345\" ]")
  private List<String> associatedClaimIds;

  @Schema(description = "Document Notes", example = "Joe Doe had previous exam.")
  private String notes;

  @Schema(description = "Payee Code", example = "00")
  private String payeeCode;

  @Schema(description = "End Product Code", example = "30DPNDCY")
  private String endProductCode;

  @Schema(description = "Regional Processing Office", example = "Buffalo")
  private String regionalProcessingOffice;

  @Schema(description = "facility Code", example = "Facility")
  private String facilityCode;

  @Schema(description = "claimant ParticipantId", example = "9320497233")
  private String claimantParticipantId;

  @Schema(description = "Source comment", example = "source comment")
  private String sourceComment; // "source comment",

  @Schema(description = "Claimant Date of Birth", example = "1972-01-01")
  private String claimantDateOfBirth;
}
