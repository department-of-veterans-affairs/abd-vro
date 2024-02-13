package gov.va.vro.mockmas.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderExamSuccess {
  private List<ConditionInfo> conditions;
  private int collectionsId;
}
