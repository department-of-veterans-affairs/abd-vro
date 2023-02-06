package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/** Model that identifies a single individual used in the security context. */
@Schema(
    name = "Person",
    description = "Model that identifies a single individual used in the security context")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class Person {

  @JsonProperty("assuranceLevel")
  private Integer assuranceLevel;

  @JsonProperty("birthDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthDate;

  @JsonProperty("correlationIds")
  @Valid
  private List<String> correlationIds = null;

  @JsonProperty("email")
  private String email;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("gender")
  private String gender;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("middleName")
  private String middleName;

  @JsonProperty("prefix")
  private String prefix;

  @JsonProperty("suffix")
  private String suffix;

  @JsonProperty("applicationID")
  private String applicationId;

  @JsonProperty("stationId")
  private String stationId;

  @JsonProperty("userID")
  private String userId;

  @JsonProperty("appToken")
  private String appToken;

  public Person assuranceLevel(Integer assuranceLevel) {
    this.assuranceLevel = assuranceLevel;
    return this;
  }

  /**
   * The person's access assurance level minimum: 0.
   *
   * @return assuranceLevel
   */
  @Min(0)
  @Schema(
      name = "assuranceLevel",
      example = "2",
      description = "The person's access assurance level")
  public Integer getAssuranceLevel() {
    return assuranceLevel;
  }

  public void setAssuranceLevel(Integer assuranceLevel) {
    this.assuranceLevel = assuranceLevel;
  }

  public Person birthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * The person's birth date.
   *
   * @return birthDate
   */
  @Valid
  @Schema(name = "birthDate", description = "The person's birth date")
  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public Person correlationIds(List<String> correlationIds) {
    this.correlationIds = correlationIds;
    return this;
  }

  /**
   * Adds correlation ids.
   *
   * @param correlationIdsItem correlation id
   * @return person
   */
  public Person addCorrelationIdsItem(String correlationIdsItem) {
    if (this.correlationIds == null) {
      this.correlationIds = new ArrayList<>();
    }
    this.correlationIds.add(correlationIdsItem);
    return this;
  }

  /**
   * The MVI correlation IDs list for the person.
   *
   * @return correlationIds
   */
  @Schema(
      name = "correlationIds",
      example =
          """
          [\"77779102^NI^200M^USVHA^P\",\"912444689^PI^200BRLS^USVBA^A\",
          \"6666345^PI^200CORP^USVBA^A\",\"1105051936^NI^200DOD^USDOD^A\",\"912444689^SS\"]
          """,
      description = "The MVI correlation IDs list for the person")
  public List<String> getCorrelationIds() {
    return correlationIds;
  }

  public void setCorrelationIds(List<String> correlationIds) {
    this.correlationIds = correlationIds;
  }

  public Person email(String email) {
    this.email = email;
    return this;
  }

  /**
   * The person's email address.
   *
   * @return email
   */
  @Schema(name = "email", example = "jane.doe@va.gov", description = "The person's email address")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Person firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * The person's first name.
   *
   * @return firstName
   */
  @Schema(name = "firstName", example = "JANE", description = "The person's first name")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Person gender(String gender) {
    this.gender = gender;
    return this;
  }

  /**
   * The person's gender.
   *
   * @return gender
   */
  @Schema(name = "gender", description = "The person's gender")
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public Person lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * The person's last name.
   *
   * @return lastName
   */
  @Schema(name = "lastName", example = "DOE", description = "The person's last name")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Person middleName(String middleName) {
    this.middleName = middleName;
    return this;
  }

  /**
   * The person's middle name.
   *
   * @return middleName
   */
  @Schema(name = "middleName", description = "The person's middle name")
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public Person prefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * The prefix for the person's full name.
   *
   * @return prefix
   */
  @Schema(name = "prefix", description = "The prefix for the person's full name")
  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Person suffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  /**
   * The suffix for the person's full name.
   *
   * @return suffix
   */
  @Schema(name = "suffix", description = "The suffix for the person's full name")
  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public Person applicationId(String applicationId) {
    this.applicationId = applicationId;
    return this;
  }

  /**
   * The application taking action on the record.
   *
   * @return applicationID
   */
  @Schema(
      name = "applicationID",
      example = "BIPCLAIMSAPI",
      description = "The application taking action on the record")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public Person stationId(String stationId) {
    this.stationId = stationId;
    return this;
  }

  /**
   * The number representing the Regional Office of the person or service taking action on the.
   * record
   *
   * @return stationID
   */
  @Schema(
      name = "stationID",
      example = "281",
      description =
          """
          The number representing the Regional Office of the person or service taking action
          on the record
          """)
  public String getStationId() {
    return stationId;
  }

  public void setStationId(String stationId) {
    this.stationId = stationId;
  }

  public Person userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * The name associated with the person or service taking action on the record.
   *
   * @return userID
   */
  @Schema(
      name = "userID",
      example = "BIPCLAIMSYSACCT",
      description = "The name associated with the person or service taking action on the record")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Person appToken(String appToken) {
    this.appToken = appToken;
    return this;
  }

  /**
   * Auditing and authentication credentials.
   *
   * @return appToken
   */
  @Schema(name = "appToken", description = "Auditing and authentication credentials")
  public String getAppToken() {
    return appToken;
  }

  public void setAppToken(String appToken) {
    this.appToken = appToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return Objects.equals(this.assuranceLevel, person.assuranceLevel)
        && Objects.equals(this.birthDate, person.birthDate)
        && Objects.equals(this.correlationIds, person.correlationIds)
        && Objects.equals(this.email, person.email)
        && Objects.equals(this.firstName, person.firstName)
        && Objects.equals(this.gender, person.gender)
        && Objects.equals(this.lastName, person.lastName)
        && Objects.equals(this.middleName, person.middleName)
        && Objects.equals(this.prefix, person.prefix)
        && Objects.equals(this.suffix, person.suffix)
        && Objects.equals(this.applicationId, person.applicationId)
        && Objects.equals(this.stationId, person.stationId)
        && Objects.equals(this.userId, person.userId)
        && Objects.equals(this.appToken, person.appToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        assuranceLevel,
        birthDate,
        correlationIds,
        email,
        firstName,
        gender,
        lastName,
        middleName,
        prefix,
        suffix,
        applicationId,
        stationId,
        userId,
        appToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Person {\n");
    sb.append("    assuranceLevel: ").append(toIndentedString(assuranceLevel)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    correlationIds: ").append(toIndentedString(correlationIds)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
    sb.append("    suffix: ").append(toIndentedString(suffix)).append("\n");
    sb.append("    applicationID: ").append(toIndentedString(applicationId)).append("\n");
    sb.append("    stationID: ").append(toIndentedString(stationId)).append("\n");
    sb.append("    userID: ").append(toIndentedString(userId)).append("\n");
    sb.append("    appToken: ").append(toIndentedString(appToken)).append("\n");
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
