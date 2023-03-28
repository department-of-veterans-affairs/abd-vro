package gov.va.vro.model.bgs;

import lombok.Data;

@Data
public class BgsApiClientModel {

  String claimId;
  String note;

  private int statusCode;
  String statusMessage;
}
