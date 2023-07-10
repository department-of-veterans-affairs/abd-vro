package gov.va.vro.services.xample;

public class Util {
  /**
   * @return A string containing the file name and line number of where the function was called
   *     from.
   */
  public static String fl() {
    StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
    return String.format("(%d,%s)", ste.getFileName(), ste.getLineNumber());
  }
}
