package gov.va.starter.example.service.spi.db.model;

import lombok.Data;

@Data
class EvidenceSummaryDocument {

  private String documentName;
  private int evidenceCount;
}
