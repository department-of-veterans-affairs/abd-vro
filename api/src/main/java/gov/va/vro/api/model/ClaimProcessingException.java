package gov.va.vro.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ClaimProcessingException extends Exception {

  private final String claimSubmissionId;
  private final HttpStatus httpStatus;
  private final String message;
}
