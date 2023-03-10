package gov.va.vro.mockbipclaims.model.bip;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;

/** Model that identifies a single individual used in the security context. */
@Schema(
    name = "Person",
    description = "Model that identifies a single individual used in the security context")
@Data
public class Person {
  private Integer assuranceLevel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthDate;

  @Valid private List<String> correlationIds = null;

  private String email;

  private String firstName;

  private String gender;

  private String lastName;

  private String middleName;

  private String prefix;

  private String suffix;

  @JsonProperty("applicationID")
  private String applicationId;

  @JsonProperty("stationId")
  private String stationId;

  @JsonProperty("userID")
  private String userId;

  private String appToken;
}
