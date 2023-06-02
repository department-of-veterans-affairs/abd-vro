package gov.va.vro.bip.model;

/**
 * ID types allowed for BIP Claims Evidence file upload.
 *
 * @author warren @Date 12/1/22
 */
public enum FileIdType {
  FILENUMBER,
  SSN,
  PARTICIPANT_ID,
  EDIPI;

  /**
   * Get ID type.
   *
   * @param idString ID string.
   * @return return.
   */
  public static FileIdType getIdType(String idString) {
    String value = idString.toUpperCase();
    if (FILENUMBER.name().equals(value)) {
      return FILENUMBER;
    } else if (SSN.name().equals(value)) {
      return SSN;
    } else if (PARTICIPANT_ID.name().equals(value)) {
      return PARTICIPANT_ID;
    } else if (EDIPI.name().equals(value)) {
      return EDIPI;
    } else {
      return null;
    }
  }
}
