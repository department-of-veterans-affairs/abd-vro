package gov.va.vro.mockbipclaims.model.bip.request;

import gov.va.vro.mockbipclaims.model.bip.ExistingContention;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** UpdateContentionsRequest. */
@Data
public class UpdateContentionsRequest {
  @Valid private List<ExistingContention> updateContentions = null;

  /**
   * Adds a contention to the response.
   *
   * @param contention Contention to add
   */
  public void addUpdateContentionsItem(ExistingContention contention) {
    if (updateContentions == null) {
      updateContentions = new ArrayList<>();
    }
    updateContentions.add(contention);
  }
}
