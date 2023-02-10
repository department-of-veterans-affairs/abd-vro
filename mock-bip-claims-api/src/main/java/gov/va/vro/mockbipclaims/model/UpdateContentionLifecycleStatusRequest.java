package gov.va.vro.mockbipclaims.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

/** Request to update contention lifecycle status. */
@Schema(
    name = "UpdateContentionLifecycleStatusRequest",
    description = "Request to update contention lifecycle status.")
@Data
public class UpdateContentionLifecycleStatusRequest {
  @Valid private List<UpdateContentionLifecycleStatusRequestItem> contentions = new ArrayList<>();
}
