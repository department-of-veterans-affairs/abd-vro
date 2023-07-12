package gov.va.vro.bip.service;

import gov.va.vro.bip.model.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Does same thing as BipApiServiceTest but through RMQ instance. Assumes RMQ broker is available
 * locally.
 */
//@Disabled("needs an RMQ broker, which is not available in github build env.")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
class RMQIntegrationTest2 {
  //@Autowired BipApiService service;
  @MockBean BipApiService service;
  @Autowired RabbitTemplate rabbitTemplate;
  @Autowired RabbitAdmin rabbitAdmin;

  @Autowired
  private ApplicationContext context;

  @Test
  void testUpdateClaimStatus(  @Value("${updateClaimStatusQueue}") String updateClaimStatusQueue){
    String qName = updateClaimStatusQueue;
    rabbitAdmin.purgeQueue(qName, true);
    RequestForUpdateClaimStatus req = new RequestForUpdateClaimStatus(ClaimStatus.RFD,1);
    BipUpdateClaimResp resp = new BipUpdateClaimResp();
    resp.statusMessage = "test pass";
    Mockito.doReturn(resp)
        .when(service)
        .updateClaimStatus(ArgumentMatchers.any(),ArgumentMatchers.any(ClaimStatus.class));

    BipUpdateClaimResp result =
        (BipUpdateClaimResp)
            rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    assertEquals(result.statusMessage, resp.statusMessage);


  }

  @Test
  void testGetClaimContentions(@Value("${getClaimContentionsQueue}")String getClaimContentionsQueue){

    rabbitAdmin.purgeQueue(getClaimContentionsQueue, true);
    long req = 42;
    BipContentionResp resp = new BipContentionResp();
    resp.statusMessage = "test pass";
    Mockito.doReturn(resp)
        .when(service)
        .getClaimContentions(req);

    BipContentionResp result =
        (BipContentionResp)
            rabbitTemplate.convertSendAndReceive(exchangeName, getClaimContentionsQueue, req);

    assertEquals(result.statusMessage, resp.statusMessage);
  }




  @Value("${exchangeName}")
  String exchangeName;

}
