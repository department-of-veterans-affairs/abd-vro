package gov.va.vro.model.bipevidence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.validation.Valid;

/**
 * Details about a file&#39;s conversion. This is optional, and will only be present on responses
 * where the file has been converted.
 */
@Schema(
    name = "conversionInformation",
    description =
        "Details about a file's conversion. This is optional, and will only be present on responses where the file has been converted.")
@JsonTypeName("conversionInformation")
public class ConversionInformation {

  @JsonProperty("preprocessed")
  private ConversionInfo preprocessed;

  @JsonProperty("converted")
  private ConversionInfo converted;

  public ConversionInformation preprocessed(ConversionInfo preprocessed) {
    this.preprocessed = preprocessed;
    return this;
  }

  /**
   * Get preprocessed
   *
   * @return preprocessed
   */
  @Valid
  @Schema(name = "preprocessed", required = false)
  public ConversionInfo getPreprocessed() {
    return preprocessed;
  }

  public void setPreprocessed(ConversionInfo preprocessed) {
    this.preprocessed = preprocessed;
  }

  public ConversionInformation converted(ConversionInfo converted) {
    this.converted = converted;
    return this;
  }

  /**
   * Get converted
   *
   * @return converted
   */
  @Valid
  @Schema(name = "converted", required = false)
  public ConversionInfo getConverted() {
    return converted;
  }

  public void setConverted(ConversionInfo converted) {
    this.converted = converted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConversionInformation conversionInformation = (ConversionInformation) o;
    return Objects.equals(this.preprocessed, conversionInformation.preprocessed)
        && Objects.equals(this.converted, conversionInformation.converted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(preprocessed, converted);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConversionInformation {\n");
    sb.append("    preprocessed: ").append(toIndentedString(preprocessed)).append("\n");
    sb.append("    converted: ").append(toIndentedString(converted)).append("\n");
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
