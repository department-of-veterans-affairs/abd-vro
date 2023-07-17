package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import gov.va.vro.bip.model.*;
import gov.va.vro.bip.service.BipApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Does same thing as BipApiServiceTest but through RMQ instance. Assumes RMQ broker is available
 * locally.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
class RMQIntegrationTest {
  @MockBean BipApiService service;
  @Autowired RabbitTemplate rabbitTemplate;
  @Autowired RabbitAdmin rabbitAdmin;

  @Test
  void testUpdateClaimStatus(@Value("${updateClaimStatusQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, true);
    RequestForUpdateClaimStatus req = new RequestForUpdateClaimStatus(ClaimStatus.RFD, 1);
    BipUpdateClaimResp resp = new BipUpdateClaimResp();
    resp.statusMessage = "test pass";
    Mockito.when(service.updateClaimStatus(Mockito.anyLong(),Mockito.any())).thenReturn(resp);

    BipUpdateClaimResp result =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertEquals(result.statusMessage, resp.statusMessage);
    rabbitAdmin.purgeQueue(qName, true);
  }

  @Test
  void testGetClaimContentions(@Value("${getClaimContentionsQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, true);
    long req = 42;
    BipContentionResp resp = new BipContentionResp();
    List<ClaimContention> result = new ArrayList<ClaimContention>();
    result.add(new ClaimContention());

    resp.statusMessage = "test pass";
    Mockito.when(service.getClaimContentions(Mockito.any())).thenReturn(result);
//    Mockito.doReturn(result).when(service).getClaimContentions(req);

    BipContentionResp response =
        (BipContentionResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    assertTrue(response.getContentions().size() == 1);
    rabbitAdmin.purgeQueue(qName, true);
  }

  @Test
  void testGetClaimDetails(@Value("${getClaimDetailsQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, true);
    long req = 42;
    BipClaim result = new BipClaim();
    result.setPhase("phase");
//    Mockito.doReturn(result).when(service).getClaimDetails(req);
    Mockito.when(service.getClaimDetails(Mockito.eq(req))).thenReturn(result);


    BipClaim response = (BipClaim) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertEquals(response.getPhase(), result.getPhase());
    rabbitAdmin.purgeQueue(qName, true);
  }

  @Test
  void testSetClaimToRfdStatus(@Value("${setClaimToRfdStatusQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, true);
    long req = 42;
    BipUpdateClaimResp result = new BipUpdateClaimResp();
    result.statusMessage = "msg";
//    Mockito.doReturn(result).when(service).setClaimToRfdStatus(req);
    Mockito.when(service.setClaimToRfdStatus(Mockito.eq(req))).thenReturn(result);


    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(response.statusMessage, result.statusMessage);
    rabbitAdmin.purgeQueue(qName, true);
  }

  @Test
  void testUpdateClaimContention(@Value("${updateClaimContentionQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, true);
    UpdateContentionReq req = UpdateContentionReq.builder().claimId(123).build();
    BipUpdateClaimResp result = new BipUpdateClaimResp();
    result.statusMessage = "msg";
    when(service.updateClaimContention(anyLong(), any())).thenReturn(result);

    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertEquals(response.statusMessage, result.statusMessage);
    rabbitAdmin.purgeQueue(qName, true);
  }

  @Value("${exchangeName}")
  String exchangeName;
}
