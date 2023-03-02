package gov.va.vro.mockbipce.controller;

import gov.va.vro.mockbipce.api.DocumentTypesApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DocumentTypesController implements DocumentTypesApi {

  @SneakyThrows
  @Override
  public ResponseEntity<String> getDocumentTypes() {
    return new ResponseEntity<>("[]", HttpStatus.OK);
  }
}
