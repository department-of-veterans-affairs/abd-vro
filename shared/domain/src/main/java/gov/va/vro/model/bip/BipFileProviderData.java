package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The data object is used in BIP Evidence File Upload API.
 *
 * @author warren @Date 11/11/22
 */
@Getter
@Setter
public class BipFileProviderData {
  private String contentSource; // VBMS
  private String claimantFirstName;
  private String claimantMiddleInitial; // e.g, M
  private String claimantLastName;
  private String claimantSsn;
  private int benefitTypeId; // eg. 10,
  private int documentTypeId; // e.g., 131,
  private String dateVaReceivedDocument; // "2022-02-01",
  private String subject;
  private List<String> contentions; // e.g., [ "contention 1]
  private boolean actionable;
  private List<String> associatedClaimIds; // e.g.,  [ "1" ],
  private List<String>
      notes; // e.g., "[This is a note for a document, These replace editing the document summary]"
  private String payeeCode; // e.g., "00"
  private String endProductCode; // e.g., "130DPNDCY",
  private String regionalProcessingOffice; // "Buffalo",
  private String facilityCode; // "Facility",
  private String claimantParticipantId; // "000000000",
  private String sourceComment; // "source comment",
  private String claimantDateOfBirth; // "2022-01-01"
}
