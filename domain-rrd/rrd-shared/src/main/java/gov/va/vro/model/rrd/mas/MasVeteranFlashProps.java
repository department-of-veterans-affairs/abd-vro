package gov.va.vro.model.rrd.mas;

import lombok.Getter;

@Getter
public class MasVeteranFlashProps {

  private static MasVeteranFlashProps INSTANCE;
  private String[] agentOrangeFlashIds = {};

  private MasVeteranFlashProps(String[] agentOrangeFlashIds) {
    this.agentOrangeFlashIds = agentOrangeFlashIds;
  }
  ;

  public static MasVeteranFlashProps getInstance(String[] agentOrangeFlashIds) {
    if (INSTANCE == null) {
      INSTANCE = new MasVeteranFlashProps(agentOrangeFlashIds);
    }
    return INSTANCE;
  }

  public static MasVeteranFlashProps getInstance() {
    return INSTANCE;
  }
}
