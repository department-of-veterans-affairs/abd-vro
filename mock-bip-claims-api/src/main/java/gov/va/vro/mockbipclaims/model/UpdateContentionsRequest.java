package gov.va.vro.mockbipclaims.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

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
