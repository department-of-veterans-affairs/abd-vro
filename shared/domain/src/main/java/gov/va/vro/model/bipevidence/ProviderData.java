package gov.va.vro.model.bipevidence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.validation.Valid;

/** Provider Data associated to file. */
@Schema(name = "providerData", description = "Provider Data associated to file.")
@JsonTypeName("providerData")
public class ProviderData {

  @JsonProperty("modifiedDateTime")
  private String modifiedDateTime;

  @JsonProperty("dateVaReceivedDocument")
  private String dateVaReceivedDocument;

  @JsonProperty("actionable")
  private Boolean actionable;

  @JsonProperty("certified")
  private Boolean certified;

  @JsonProperty("documentTypeId")
  private Long documentTypeId;

  @JsonProperty("documentType")
  private DocumentType documentType;

  @JsonProperty("endProductCode")
  private String endProductCode;

  @JsonProperty("subject")
  private String subject;

  @JsonProperty("systemSource")
  private String systemSource;

  @JsonProperty("veteranFirstName")
  private String veteranFirstName;

  @JsonProperty("veteranLastName")
  private String veteranLastName;

  @JsonProperty("veteranMiddleName")
  private String veteranMiddleName;

  @JsonProperty("veteranSuffix")
  private String veteranSuffix;

  public ProviderData modifiedDateTime(String modifiedDateTime) {
    this.modifiedDateTime = modifiedDateTime;
    return this;
  }

  /**
   * Get modifiedDateTime
   *
   * @return modifiedDateTime
   */
  @Schema(name = "modifiedDateTime", required = false)
  public String getModifiedDateTime() {
    return modifiedDateTime;
  }

  public void setModifiedDateTime(String modifiedDateTime) {
    this.modifiedDateTime = modifiedDateTime;
  }

  public ProviderData dateVaReceivedDocument(String dateVaReceivedDocument) {
    this.dateVaReceivedDocument = dateVaReceivedDocument;
    return this;
  }

  /**
   * Get dateVaReceivedDocument
   *
   * @return dateVaReceivedDocument
   */
  @Schema(name = "dateVaReceivedDocument", required = false)
  public String getDateVaReceivedDocument() {
    return dateVaReceivedDocument;
  }

  public void setDateVaReceivedDocument(String dateVaReceivedDocument) {
    this.dateVaReceivedDocument = dateVaReceivedDocument;
  }

  public ProviderData actionable(Boolean actionable) {
    this.actionable = actionable;
    return this;
  }

  /**
   * Get actionable
   *
   * @return actionable
   */
  @Schema(name = "actionable", required = false)
  public Boolean getActionable() {
    return actionable;
  }

  public void setActionable(Boolean actionable) {
    this.actionable = actionable;
  }

  public ProviderData certified(Boolean certified) {
    this.certified = certified;
    return this;
  }

  /**
   * Get certified
   *
   * @return certified
   */
  @Schema(name = "certified", required = false)
  public Boolean getCertified() {
    return certified;
  }

  public void setCertified(Boolean certified) {
    this.certified = certified;
  }

  public ProviderData documentTypeId(Long documentTypeId) {
    this.documentTypeId = documentTypeId;
    return this;
  }

  /**
   * Get documentTypeId
   *
   * @return documentTypeId
   */
  @Schema(name = "documentTypeId", required = false)
  public Long getDocumentTypeId() {
    return documentTypeId;
  }

  public void setDocumentTypeId(Long documentTypeId) {
    this.documentTypeId = documentTypeId;
  }

  public ProviderData documentType(DocumentType documentType) {
    this.documentType = documentType;
    return this;
  }

  /**
   * Get documentType
   *
   * @return documentType
   */
  @Valid
  @Schema(name = "documentType", required = false)
  public DocumentType getDocumentType() {
    return documentType;
  }

  public void setDocumentType(DocumentType documentType) {
    this.documentType = documentType;
  }

  public ProviderData endProductCode(String endProductCode) {
    this.endProductCode = endProductCode;
    return this;
  }

  /**
   * Get endProductCode
   *
   * @return endProductCode
   */
  @Schema(name = "endProductCode", required = false)
  public String getEndProductCode() {
    return endProductCode;
  }

  public void setEndProductCode(String endProductCode) {
    this.endProductCode = endProductCode;
  }

  public ProviderData subject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * Get subject
   *
   * @return subject
   */
  @Schema(name = "subject", required = false)
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public ProviderData systemSource(String systemSource) {
    this.systemSource = systemSource;
    return this;
  }

  /**
   * Get systemSource
   *
   * @return systemSource
   */
  @Schema(name = "systemSource", required = false)
  public String getSystemSource() {
    return systemSource;
  }

  public void setSystemSource(String systemSource) {
    this.systemSource = systemSource;
  }

  public ProviderData veteranFirstName(String veteranFirstName) {
    this.veteranFirstName = veteranFirstName;
    return this;
  }

  /**
   * Get veteranFirstName
   *
   * @return veteranFirstName
   */
  @Schema(name = "veteranFirstName", required = false)
  public String getVeteranFirstName() {
    return veteranFirstName;
  }

  public void setVeteranFirstName(String veteranFirstName) {
    this.veteranFirstName = veteranFirstName;
  }

  public ProviderData veteranLastName(String veteranLastName) {
    this.veteranLastName = veteranLastName;
    return this;
  }

  /**
   * Get veteranLastName
   *
   * @return veteranLastName
   */
  @Schema(name = "veteranLastName", required = false)
  public String getVeteranLastName() {
    return veteranLastName;
  }

  public void setVeteranLastName(String veteranLastName) {
    this.veteranLastName = veteranLastName;
  }

  public ProviderData veteranMiddleName(String veteranMiddleName) {
    this.veteranMiddleName = veteranMiddleName;
    return this;
  }

  /**
   * Get veteranMiddleName
   *
   * @return veteranMiddleName
   */
  @Schema(name = "veteranMiddleName", required = false)
  public String getVeteranMiddleName() {
    return veteranMiddleName;
  }

  public void setVeteranMiddleName(String veteranMiddleName) {
    this.veteranMiddleName = veteranMiddleName;
  }

  public ProviderData veteranSuffix(String veteranSuffix) {
    this.veteranSuffix = veteranSuffix;
    return this;
  }

  /**
   * Get veteranSuffix
   *
   * @return veteranSuffix
   */
  @Schema(name = "veteranSuffix", required = false)
  public String getVeteranSuffix() {
    return veteranSuffix;
  }

  public void setVeteranSuffix(String veteranSuffix) {
    this.veteranSuffix = veteranSuffix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProviderData providerData = (ProviderData) o;
    return Objects.equals(this.modifiedDateTime, providerData.modifiedDateTime)
        && Objects.equals(this.dateVaReceivedDocument, providerData.dateVaReceivedDocument)
        && Objects.equals(this.actionable, providerData.actionable)
        && Objects.equals(this.certified, providerData.certified)
        && Objects.equals(this.documentTypeId, providerData.documentTypeId)
        && Objects.equals(this.documentType, providerData.documentType)
        && Objects.equals(this.endProductCode, providerData.endProductCode)
        && Objects.equals(this.subject, providerData.subject)
        && Objects.equals(this.systemSource, providerData.systemSource)
        && Objects.equals(this.veteranFirstName, providerData.veteranFirstName)
        && Objects.equals(this.veteranLastName, providerData.veteranLastName)
        && Objects.equals(this.veteranMiddleName, providerData.veteranMiddleName)
        && Objects.equals(this.veteranSuffix, providerData.veteranSuffix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        modifiedDateTime,
        dateVaReceivedDocument,
        actionable,
        certified,
        documentTypeId,
        documentType,
        endProductCode,
        subject,
        systemSource,
        veteranFirstName,
        veteranLastName,
        veteranMiddleName,
        veteranSuffix);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProviderData {\n");
    sb.append("    modifiedDateTime: ").append(toIndentedString(modifiedDateTime)).append("\n");
    sb.append("    dateVaReceivedDocument: ")
        .append(toIndentedString(dateVaReceivedDocument))
        .append("\n");
    sb.append("    actionable: ").append(toIndentedString(actionable)).append("\n");
    sb.append("    certified: ").append(toIndentedString(certified)).append("\n");
    sb.append("    documentTypeId: ").append(toIndentedString(documentTypeId)).append("\n");
    sb.append("    documentType: ").append(toIndentedString(documentType)).append("\n");
    sb.append("    endProductCode: ").append(toIndentedString(endProductCode)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    systemSource: ").append(toIndentedString(systemSource)).append("\n");
    sb.append("    veteranFirstName: ").append(toIndentedString(veteranFirstName)).append("\n");
    sb.append("    veteranLastName: ").append(toIndentedString(veteranLastName)).append("\n");
    sb.append("    veteranMiddleName: ").append(toIndentedString(veteranMiddleName)).append("\n");
    sb.append("    veteranSuffix: ").append(toIndentedString(veteranSuffix)).append("\n");
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
