package gov.va.vro.service.db.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

@Data
public class ClaimRequest {

  @NotNull private String claimId;

  @NotNull private String idType;

  @NotNull private String incomingStatus = "submission";

  @NotNull private Veteran veteran;

  private List<Contention> contentions = new ArrayList<>();
}
