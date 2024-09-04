package gov.va.vro.mockbipclaims.model.bip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Schema(
    name = "SpecialIssueType",
    description =
        """
        An issue type that is used to describe a special type of contention
        """)
@Data
public class SpecialIssueType {
  @NotNull private String name;
  @NotNull private String code;
  private String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime deactiveDate;
}
