package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ClaimDetailResponseAllOf. */
@JsonTypeName("ClaimDetailResponse_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimDetailResponseAllOf {

  @JsonProperty("claim")
  private ClaimDetail claim;

  public ClaimDetailResponseAllOf claim(ClaimDetail claim) {
    this.claim = claim;
    return this;
  }

  /**
   * Get claim.
   *
   * @return claim
   */
  @Valid
  @Schema(name = "claim")
  public ClaimDetail getClaim() {
    return claim;
  }

  public void setClaim(ClaimDetail claim) {
    this.claim = claim;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimDetailResponseAllOf claimDetailResponseAllOf = (ClaimDetailResponseAllOf) o;
    return Objects.equals(this.claim, claimDetailResponseAllOf.claim);
  }

  @Override
  public int hashCode() {
    return Objects.hash(claim);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimDetailResponseAllOf {\n");
    sb.append("    claim: ").append(toIndentedString(claim)).append("\n");
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
