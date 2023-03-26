package gov.va.vro.controller.rrd.exception;

public class DisallowedPatternException extends IllegalArgumentException {
  public DisallowedPatternException() {
    super(
        "Disallowed patterns were found in the Request Body."
            + " Please sanitize your Request Body input data and try again.");
  }
}
