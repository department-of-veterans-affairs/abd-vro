package gov.va.vro.mockbipclaims.model.store;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ModifyingActionStore {
  private List<String> modifyingActions = new ArrayList<>();

  public void addAction(ModifyingActionEnum action) {
    String description = action.getDescription();
    modifyingActions.add(description);
  }
}
