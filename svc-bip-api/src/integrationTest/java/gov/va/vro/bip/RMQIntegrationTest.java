package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipContentionResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimContention;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.RequestForUpdateClaimStatus;
import gov.va.vro.bip.model.UpdateContentionReq;
import gov.va.vro.bip.service.BipApiService;
import gov.va.vro.bip.service.RMQController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

@Disabled("Currently fails on first run, but passes on second run")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
class RMQIntegrationTest {
  @MockBean BipApiService service;
  @Autowired RMQController controller;
  @Autowired RabbitTemplate rabbitTemplate;
  @Autowired RabbitAdmin rabbitAdmin;

  @Value("${exchangeName}")
  String exchangeName;

  @Test
  void testUpdateClaimStatus(@Value("${updateClaimStatusQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, false);
    RequestForUpdateClaimStatus req = new RequestForUpdateClaimStatus(ClaimStatus.RFD, 1);
    BipUpdateClaimResp resp = new BipUpdateClaimResp();
    resp.statusMessage = "test pass";
    when(service.updateClaimStatus(anyLong(), any(ClaimStatus.class))).thenReturn(resp);

    BipUpdateClaimResp result =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertEquals(result.statusMessage, resp.statusMessage);
    rabbitAdmin.purgeQueue(qName, false);
  }

  @Test
  void testGetClaimContentions(@Value("${getClaimContentionsQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, false);
    long req = 42;
    BipContentionResp resp = new BipContentionResp();
    List<ClaimContention> result = new ArrayList<ClaimContention>();
    result.add(new ClaimContention());

    resp.statusMessage = "test pass";
    Mockito.when(service.getClaimContentions(Mockito.eq(req))).thenReturn(result);

    BipContentionResp response =
        (BipContentionResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    assertTrue(response.getContentions().size() == 1);
    rabbitAdmin.purgeQueue(qName, false);
  }

  @Test
  void testGetClaimDetails(@Value("${getClaimDetailsQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, false);

    long req = 42;
    BipClaim result = new BipClaim();
    result.setPhase("phase");
    Mockito.when(service.getClaimDetails(Mockito.anyLong())).thenReturn(result);

    BipClaim response = (BipClaim) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertEquals(response.getPhase(), result.getPhase());
    rabbitAdmin.purgeQueue(qName, false);
  }

  @Test
  void testSetClaimToRfdStatus(@Value("${setClaimToRfdStatusQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, false);
    long req = 42L;
    BipUpdateClaimResp result = new BipUpdateClaimResp();
    result.statusMessage = "msg";
    Mockito.when(service.setClaimToRfdStatus(Mockito.anyLong())).thenReturn(result);

    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(response.statusMessage, result.statusMessage);
    rabbitAdmin.purgeQueue(qName, false);
  }

  @Test
  void testUpdateClaimContention(@Value("${updateClaimContentionQueue}") String qName) {
    rabbitAdmin.purgeQueue(qName, false);
    UpdateContentionReq req = UpdateContentionReq.builder().claimId(123).build();
    BipUpdateClaimResp result = new BipUpdateClaimResp();
    result.statusMessage = "msg";
    when(service.updateClaimContention(anyLong(), any(UpdateContentionReq.class)))
        .thenReturn(result);

    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);

    Assertions.assertEquals(response.statusMessage, result.statusMessage);
    rabbitAdmin.purgeQueue(qName, false);
  }
}
