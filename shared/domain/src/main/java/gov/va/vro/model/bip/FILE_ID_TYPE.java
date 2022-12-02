package gov.va.vro.model.bip;

/**
 * ID types allowed for BIP Claims Evidence file upload.
 *
 * @author warren @Date 12/1/22
 */
public enum FILE_ID_TYPE {
  FILENUMBER,
  SSN,
  PARTICIPANT_ID,
  EDIPI;

  public static FILE_ID_TYPE getIDType(String idString) {
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
