package gov.va.vro.services.xample;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceB {
  @SneakyThrows
  public SomeDtoModel processRequest(SomeDtoModel model) {
    log.info("Simulating serviceB processing in the background... delaying");
    Thread.sleep(1_000);

    model.status(StatusValue.DONE);
    return model;
  }
}
