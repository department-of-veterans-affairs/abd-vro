package gov.va.vro.model.bipevidence.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** Claim Evidence API Provider Data. */
@Schema(name = "uploadProviderDataRequest", description = "Claim Evidence API Provider Data.")
@JsonTypeName("uploadProviderDataRequest")
public class UploadProviderDataRequest {

  @JsonProperty("contentSource")
  private String contentSource;

  @JsonProperty("documentTypeId")
  private Integer documentTypeId;

  @JsonProperty("dateVaReceivedDocument")
  private String dateVaReceivedDocument;

  @JsonProperty("subject")
  private String subject;

  @JsonProperty("contention")
  private String contention;

  @JsonProperty("alternativeDocumentTypeId")
  private Integer alternativeDocumentTypeId;

  @JsonProperty("actionable")
  private Boolean actionable = false;

  @JsonProperty("associatedClaimId")
  private String associatedClaimId;

  public UploadProviderDataRequest contentSource(String contentSource) {
    this.contentSource = contentSource;
    return this;
  }

  /**
   * String field designating the originating source of the content being uploaded.
   *
   * @return contentSource
   */
  @NotNull
  @Pattern(regexp = "^[a-zA-Z0-9\\'\\,\\s.\\-\\_\\|/@\\(\\)]*$")
  @Schema(
      name = "contentSource",
      description =
          "String field designating the originating source of the content being uploaded.",
      required = true)
  public String getContentSource() {
    return contentSource;
  }

  public void setContentSource(String contentSource) {
    this.contentSource = contentSource;
  }

  public UploadProviderDataRequest documentTypeId(Integer documentTypeId) {
    this.documentTypeId = documentTypeId;
    return this;
  }

  /**
   * Number field correlating to a Claim Evidence document type ID. Document types primary use is
   * loosely categorizing their contents. minimum: 1
   *
   * @return documentTypeId
   */
  @NotNull
  @Min(1)
  @Schema(
      name = "documentTypeId",
      description =
          """
          Number field correlating to a Claim Evidence document type ID.
          Document types primary use is loosely categorizing their contents.
          """,
      required = true)
  public Integer getDocumentTypeId() {
    return documentTypeId;
  }

  public void setDocumentTypeId(Integer documentTypeId) {
    this.documentTypeId = documentTypeId;
  }

  public UploadProviderDataRequest dateVaReceivedDocument(String dateVaReceivedDocument) {
    this.dateVaReceivedDocument = dateVaReceivedDocument;
    return this;
  }

  /**
   * Date field indicating the date the VA received the document. This can be any date in format of
   * YYYY-MM-DD from 1900 until today
   *
   * @return dateVaReceivedDocument
   */
  @NotNull
  @Pattern(regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")
  @Size(min = 10, max = 10)
  @Schema(
      name = "dateVaReceivedDocument",
      description =
          """
          Date field indicating the date the VA received the document.
          This can be any date in format of YYYY-MM-DD from 1900 until today.
          """,
      required = true)
  public String getDateVaReceivedDocument() {
    return dateVaReceivedDocument;
  }

  public void setDateVaReceivedDocument(String dateVaReceivedDocument) {
    this.dateVaReceivedDocument = dateVaReceivedDocument;
  }

  public UploadProviderDataRequest subject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * Free text describing the document. This is primarily notes used to assist claim developers.
   *
   * @return subject
   */
  @Pattern(regexp = "^[a-zA-Z0-9\\s.\\-_|\\Q\\\\E@#~=%,;?!'\"`():$+*^\\[\\]&<>{}\\Q/\\E]*$")
  @Size(max = 256)
  @Schema(
      name = "subject",
      description =
          """
          Free text describing the document.
          This is primarily notes used to assist claim developers.
          """,
      required = false)
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public UploadProviderDataRequest contention(String contention) {
    this.contention = contention;
    return this;
  }

  /**
   * Contention name associated to the document. The document type must be a 526 type ID.
   *
   * @return contention
   */
  @Pattern(regexp = "^[a-zA-Z0-9\\s.\\-_|\\Q\\\\E@#~=%,;?!'\"`():$+*^\\[\\]&<>{}\\Q/\\E]*$")
  @Size(min = 1, max = 128)
  @Schema(
      name = "contention",
      description =
          "Contention name associated to the document. The document type must be a 526 type ID.",
      required = false)
  public String getContention() {
    return contention;
  }

  public void setContention(String contention) {
    this.contention = contention;
  }

  public UploadProviderDataRequest alternativeDocumentTypeId(Integer alternativeDocumentTypeId) {
    this.alternativeDocumentTypeId = alternativeDocumentTypeId;
    return this;
  }

  /**
   * Integer which relates to a document type Id. minimum: 0
   *
   * @return alternativeDocumentTypeId
   */
  @Min(0)
  @Schema(
      name = "alternativeDocumentTypeId",
      description = "Integer which relates to a document type Id.",
      required = false)
  public Integer getAlternativeDocumentTypeId() {
    return alternativeDocumentTypeId;
  }

  public void setAlternativeDocumentTypeId(Integer alternativeDocumentTypeId) {
    this.alternativeDocumentTypeId = alternativeDocumentTypeId;
  }

  public UploadProviderDataRequest actionable(Boolean actionable) {
    this.actionable = actionable;
    return this;
  }

  /**
   * Boolean true/false for if the document is considered 'actionable' or whether claim action can
   * be taken based on the content.
   *
   * @return actionable
   */
  @Schema(
      name = "actionable",
      description =
          """
          Boolean true/false for if the document is considered 'actionable'
          or whether claim action can be taken based on the content.
          """,
      required = false)
  public Boolean getActionable() {
    return actionable;
  }

  public void setActionable(Boolean actionable) {
    this.actionable = actionable;
  }

  public UploadProviderDataRequest associatedClaimId(String associatedClaimId) {
    this.associatedClaimId = associatedClaimId;
    return this;
  }

  /**
   * Id of associated claim.
   *
   * @return associatedClaimId
   */
  @Pattern(regexp = "^[a-zA-Z0-9\\s.\\-_|\\Q\\\\E@#~=%,;?!'\"`():$+*^\\[\\]&<>{}\\Q/\\E]*$")
  @Size(min = 1, max = 128)
  @Schema(name = "associatedClaimId", description = "Id of associated claim.", required = false)
  public String getAssociatedClaimId() {
    return associatedClaimId;
  }

  public void setAssociatedClaimId(String associatedClaimId) {
    this.associatedClaimId = associatedClaimId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UploadProviderDataRequest uploadProviderDataRequest = (UploadProviderDataRequest) o;
    return Objects.equals(this.contentSource, uploadProviderDataRequest.contentSource)
        && Objects.equals(this.documentTypeId, uploadProviderDataRequest.documentTypeId)
        && Objects.equals(
            this.dateVaReceivedDocument, uploadProviderDataRequest.dateVaReceivedDocument)
        && Objects.equals(this.subject, uploadProviderDataRequest.subject)
        && Objects.equals(this.contention, uploadProviderDataRequest.contention)
        && Objects.equals(
            this.alternativeDocumentTypeId, uploadProviderDataRequest.alternativeDocumentTypeId)
        && Objects.equals(this.actionable, uploadProviderDataRequest.actionable)
        && Objects.equals(this.associatedClaimId, uploadProviderDataRequest.associatedClaimId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        contentSource,
        documentTypeId,
        dateVaReceivedDocument,
        subject,
        contention,
        alternativeDocumentTypeId,
        actionable,
        associatedClaimId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UploadProviderDataRequest {\n");
    sb.append("    contentSource: ").append(toIndentedString(contentSource)).append("\n");
    sb.append("    documentTypeId: ").append(toIndentedString(documentTypeId)).append("\n");
    sb.append("    dateVaReceivedDocument: ")
        .append(toIndentedString(dateVaReceivedDocument))
        .append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    contention: ").append(toIndentedString(contention)).append("\n");
    sb.append("    alternativeDocumentTypeId: ")
        .append(toIndentedString(alternativeDocumentTypeId))
        .append("\n");
    sb.append("    actionable: ").append(toIndentedString(actionable)).append("\n");
    sb.append("    associatedClaimId: ").append(toIndentedString(associatedClaimId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
