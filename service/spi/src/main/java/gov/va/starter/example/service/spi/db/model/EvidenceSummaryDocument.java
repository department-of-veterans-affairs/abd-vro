package gov.va.starter.example.service.spi.db.model;

import lombok.Data;

@Data
public class EvidenceSummaryDocument {

  private String documentName;
  private int evidenceCount;
}
