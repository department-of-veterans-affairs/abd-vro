package gov.va.vro.mockbipclaims.model.bip.request;

import gov.va.vro.mockbipclaims.model.bip.Contention;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateContentionsRequest {
  @Valid private List<Contention> createContentions = null;

  public void addContention(Contention contention) {
    if (createContentions == null) {
      createContentions = new ArrayList<>();
    }
    createContentions.add(contention);
  }
}
