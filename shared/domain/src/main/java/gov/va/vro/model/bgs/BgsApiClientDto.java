package gov.va.vro.model.bgs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BgsApiClientDto {

  final String vbmsClaimId;
  final String veteranParticipantId;

  public List<String> veteranNotes = new ArrayList<>();
  public List<String> claimNotes = new ArrayList<>();

  int statusCode;
  String statusMessage;
}
