package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "exam_order")
public class ExamOrderEntity extends BaseEntity {

  private String collectionId;

  private String status;
}
