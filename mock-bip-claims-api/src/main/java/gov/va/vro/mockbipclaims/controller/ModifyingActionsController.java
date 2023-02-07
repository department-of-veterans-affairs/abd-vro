package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.api.ModifyingActionsApi;
import gov.va.vro.mockbipclaims.model.store.ModifyingActionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ModifyingActionsController implements ModifyingActionsApi {
  @Autowired private ModifyingActionStore store;

  @Override
  public ResponseEntity<String[]> getModifyingActions() {
    List<String> actions = store.getModifyingActions();
    String[] body = actions.toArray(new String[0]);
    ResponseEntity<String[]> response = new ResponseEntity<>(body, HttpStatus.OK);
    return response;
  }
}
