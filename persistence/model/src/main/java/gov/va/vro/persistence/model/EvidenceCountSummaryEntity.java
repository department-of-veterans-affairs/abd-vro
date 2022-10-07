package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "")
public class EvidenceCountSummaryEntity extends BaseEntity {

  private int totalBpReadings;
  private int recentBpReadings;
  private int medicationsCount;
}
